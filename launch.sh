#!/bin/bash
mvn package && java -jar target/rufus-1.0-SNAPSHOT.jar server config.yml
