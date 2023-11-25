package main

import (
	"html/template"
	"log"
	"net/http"

	"github.com/jtanza/rufus/internal"
)

func main() {
	http.Handle("/", http.FileServer(http.Dir("./web/html")))

	http.HandleFunc("/feed", func(rw http.ResponseWriter, r *http.Request) {
		articles := make([]internal.Article, 0)
		for _, feed := range internal.Feeds() {
			articles = append(articles, internal.Articles(feed)...)
		}

		t, _ := template.ParseFiles("web/html/feed.html")
		t.Execute(rw, articles)
	})

	log.Fatal(http.ListenAndServe(":3333", nil))
}
