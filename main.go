package main

import (
	"log"
	"net/http"

	"github.com/jtanza/rufus/internal"
)

func main() {
	store, err := internal.NewStore()
	if err != nil {
		log.Fatal(err)
	}
	go store.ExpireStore()

	internal.Routes(store)
	log.Fatal(http.ListenAndServe(":3333", nil))
}
