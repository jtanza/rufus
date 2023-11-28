package internal

import (
	"net/http"
	"strings"
	"text/template"
)

func Routes(store Store) {
	http.Handle("/", http.FileServer(http.Dir("web/html")))

	http.HandleFunc("/feed", func(rw http.ResponseWriter, r *http.Request) {
		feeds := store.FrontPageFeeds()
		cached := store.CachedArticles(feeds)

		articles := make([]Article, len(cached))
		for _, value := range cached {
			articles = append(articles, value...)
		}

		t, _ := template.ParseFiles("web/html/feed.html")
		t.Execute(rw, articles)
	})

	http.HandleFunc("/article/", func(w http.ResponseWriter, r *http.Request) {
		id := strings.TrimPrefix(r.URL.Path, "/article/")
		article, err := store.GetArticle(id)

		if err != nil {
			http.Error(w, "Not Found", 404)
			return
		}

		t, _ := template.ParseFiles("web/html/article.html")
		t.Execute(w, article)
	})
}
