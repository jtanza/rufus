package internal

import (
	"log"

	"github.com/mmcdole/gofeed"
)

type Feed struct {
	Title       string `json:"title,omitempty"`
	Description string `json:"description,omitempty"`
	Link        string `json:"link,omitempty"`
}

type Article struct {
	FeedTitle   string   `json:"feedtitle,omitempty"`
	FeedLink    string   `json:"feedlink,omitempty"`
	Title       string   `json:"title,omitempty"`
	Description string   `json:"description,omitempty"`
	Content     string   `json:"content,omitempty"`
	Link        string   `json:"link,omitempty"`
	Published   string   `json:"published,omitempty"`
	Authors     []string `json:"authors,omitempty"`
}

func ParseFeed(url string) *gofeed.Feed {
	fp := gofeed.NewParser()
	feed, err := fp.ParseURL(url)
	if err != nil {
		log.Fatal(err)
	}
	return feed
}

func Articles(feedUrl string) []Article {
	fp := gofeed.NewParser()
	feed, err := fp.ParseURL(feedUrl)
	if err != nil {
		log.Fatal(err)
	}

	articles := make([]Article, 0)
	for _, item := range feed.Items {
		articles = append(articles, Article{
			feed.Title,
			feed.Link,
			item.Title,
			item.Description,
			item.Content,
			item.Link,
			item.Published,
			authors(item.Authors),
		})
	}

	return articles
}

func authors(people []*gofeed.Person) []string {
	authors := make([]string, 2)
	for _, person := range people {
		authors = append(authors, person.Name)
	}
	return authors
}
