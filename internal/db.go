package internal

import (
	"database/sql"
	"errors"
	"log"
	"time"

	_ "github.com/mattn/go-sqlite3"
)

type Store struct {
	db *sql.DB
}

type Feed struct {
	Id        int
	UUID      string
	Title     string
	Link      string
	Frontpage bool
}

type Article struct {
	UUID        string `json:"uuid,omitempty"`
	FeedTitle   string `json:"feedtitle,omitempty"`
	FeedLink    string `json:"feedlink,omitempty"`
	Title       string `json:"title,omitempty"`
	Description string `json:"description,omitempty"`
	Content     string `json:"content,omitempty"`
	Link        string `json:"link,omitempty"`
	Published   string `json:"published,omitempty"`
	Authors     string `json:"authors,omitempty"`
}

func NewStore() (Store, error) {
	db, err := sql.Open("sqlite3", "./rufus.db")
	return Store{db}, err
}

func (store *Store) FrontPageFeeds() []Feed {
	rows, err := store.db.Query("select id, uuid, title, link, frontpage from feed where frontpage = 1")
	if err != nil {
		log.Fatal(err)
	}

	return feedsFromQuery(rows)
}

func (store *Store) Feeds() []Feed {
	rows, err := store.db.Query("select id, uuid, title, link, frontpage from feed")
	if err != nil {
		log.Fatal(err)
	}

	return feedsFromQuery(rows)
}

func (store *Store) CachedArticles(feeds []Feed) map[int][]Article {
	ret := make(map[int][]Article)
	for _, feed := range feeds {
		articles := store.articlesFromCache(feed.Id)
		if len(articles) == 0 {
			log.Printf("Cache miss for feed %v, fetching articles from source", feed.Id)
			articles = FeedArticles(feed.Link)
			err := store.saveToCache(feed.Id, articles)
			if err != nil {
				log.Fatal(err)
			}
		}
		ret[feed.Id] = articles
	}

	return ret
}

// TODO cache miss issue
func (store *Store) GetArticle(uuid string) (Article, error) {
	rows, err := store.db.Query(
		"select uuid, link, title, description, content, published, authors, title, link "+
			"from article where uuid = ?", uuid)
	if err != nil {
		log.Fatal(err)
	}

	articles := articlesFromQuery(rows)
	if len(articles) == 0 {
		return Article{}, errors.New("No Article for uuid")
	}

	return articles[0], nil
}

func (store *Store) ExpireStore() {
	for {
		_, err := store.db.Exec("delete from article where expiration < (unixepoch('now'))")
		time.Sleep(10 * time.Minute)
		if err != nil {
			log.Print(err)
		}
	}
}

func feedsFromQuery(rows *sql.Rows) []Feed {
	feeds := make([]Feed, 0)
	for rows.Next() {
		var feed Feed
		if err := rows.Scan(&feed.Id, &feed.UUID, &feed.Title, &feed.Link, &feed.Frontpage); err != nil {
			log.Fatal(err)
		}
		feeds = append(feeds, feed)
	}

	return feeds
}

func articlesFromQuery(rows *sql.Rows) []Article {
	articles := make([]Article, 0)
	for rows.Next() {
		var a Article
		if err := rows.Scan(&a.UUID, &a.Link, &a.Title, &a.Description, &a.Content, &a.Published, &a.Authors, &a.FeedTitle, &a.FeedLink); err != nil {
			log.Fatal(err)
		}
		articles = append(articles, a)
	}
	return articles
}

func (store *Store) articlesFromCache(feedId int) []Article {
	rows, err := store.db.Query(
		"select a.uuid, a.link, a.title, a.description, a.content, a.published, a.authors, f.title, f.link "+
			"from article a join feed f on a.feedid = f.id "+
			"where a.expiration > (unixepoch('now')) and f.id = ?", feedId)
	if err != nil {
		log.Fatal(err)
	}

	return articlesFromQuery(rows)
}

func (store *Store) saveToCache(feedId int, articles []Article) error {
	log.Printf("Storing articles for feed %v to cache", feedId)
	for _, article := range articles {
		_, err := store.db.Exec(
			"insert into article (uuid, feedid, link, title, description, content, published, authors) "+
				"values (?, ?, ?, ?, ?, ?, ?, ?)",
			article.UUID, feedId, article.Link, article.Title, article.Description, article.Content, article.Published, article.Authors)
		if err != nil {
			return err
		}
	}
	return nil
}
