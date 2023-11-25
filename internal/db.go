package internal

import (
	"database/sql"
	"log"

	_ "github.com/mattn/go-sqlite3"
)

func Feeds() []string {
	db, err := sql.Open("sqlite3", "./rufus.db")
	if err != nil {
		log.Fatal(err)
	}
	defer db.Close()

	rows, err := db.Query("select id, link from feed")
	if err != nil {
		log.Fatal(err)
	}

	feeds := make([]string, 0)
	for rows.Next() {
		var id int
		var link string
		if err := rows.Scan(&id, &link); err != nil {
			log.Fatal(err)
		}
		feeds = append(feeds, link)
	}

	return feeds
}
