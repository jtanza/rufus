#!/bin/bash
mvn package && java -Djwt.secret="a_super_secret_hash" -jar target/rufus-1.0-SNAPSHOT.jar server config.yml
