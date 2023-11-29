package internal

import (
	"fmt"
	"log"
	"strings"

	"github.com/google/uuid"
	"github.com/mmcdole/gofeed"
)

func FeedArticles(feedUrl string) []Article {
	fp := gofeed.NewParser()
	feed, err := fp.ParseURL(feedUrl)
	if err != nil {
		log.Fatal(err)
	}

	fmt.Println(feed)

	articles := make([]Article, 0)
	for _, item := range feed.Items {
		article := Article{
			uuid.New().String(),
			feed.Title,
			feed.Link,
			item.Title,
			item.Description,
			item.Content,
			item.Link,
			item.Published,
			authors(item.Authors),
		}
		formatContent(&article)
		articles = append(articles, article)
	}

	return articles
}

func authors(people []*gofeed.Person) string {
	authors := make([]string, 0)
	for _, person := range people {
		authors = append(authors, person.Name)
	}
	return strings.Join(authors, ",")
}

func formatContent(article *Article) {
	if len(article.Content) == 0 {
		article.Content = article.Description
	}
}
