## Rufus

Rufus is a free, open-source web based RSS reader. It can be built and managed locally following the instructions below, or accessed at the [public instance](rufus.news). Rufus is written in Java atop [Dropwizard](http://www.dropwizard.io/). The frontend is vanilla javascript, with the help of [Navigo](https://github.com/krasimir/navigo) and [Mustache](https://mustache.github.io/).

### Building

The project is packaged with Maven and is built as a fat jar. 
Once the jar is built, it is possible to run the application as an HTTP server with the snapshot jar and the Dropwizard `server` command. The server command will read the supplied YAML config file and start the application. 

Clone the repo.

`$ git clone https://github.com/jtanza/rufus.git && cd rufus`

Rufus uses JWT for creating access tokens for user claims. The server key is read as an environment variable `(JWT_SECRET)` and must be set prior to server startup. For convenience, a hash can be generated with bash/md5 and set directly.

`$ export JWT_SECRET=$(echo -n a_super_secret_password | md5)`

Package the project.

`$ mvn package`

Configure the h2 database.

`$ java -jar target/rufus-1.0-SNAPSHOT.jar db migrate config.yml`

Start the server. 

`$ java -jar target/rufus-1.0-SNAPSHOT.jar server config.yml`

Once the application is up it will be listening on port `8080` and can be accessed in the browser at `localhost:8080`.

Et voila, you're done, enjoy!

P.S. On all subsequent launches (barring any including database migrations) it is simply enough to run the `./launch` script to start the application.

#### Overview & Getting Started
Rufus leverages the [ROME](https://rometools.github.io/rome/) framework to parse user's syndication feeds for use throughout the rest of the application.

The application operates on both anonymous and authenticated user sessions. Anonymous sessions return data from a predefined collection of 'public' RSS feeds. 

Syndication feeds (`Source`s internally) are displayed as `Article`s on the frontend. After initial load, articles are cached internally with a short TTL as a trade-off on application speed/ real-time source updates. All user facing article functionality is grouped within the `ArticleResource` and exposed through requests at `/api/articles/*` 

Also, please note that this project is still under development and as such much functionality may be currently broken/ missing. Below there is a small list of todo items I plan on implementing as quickly as possible (:

### Database

Rufus uses H2 and JDBI on the persistence layer. If user's wish to inspect the contents of the application's database, it is as simple as downloading the H2 jar/zip and launching the shell console (H2 also supports a browser console, which is not covered here). 

Assuming the user has setup the application following the build instructions above, the H2 database will have been automatically created and initiated with the requisite schema in the `/target` subdirectory. Below are some instructions on setting up the H2 console. 

Download the zip from H2, and unzip to bin or wherever you see fit. 

`$ unzip h2-2017-06-10.zip -d destination_dir`

After unpackaging, launch the shell console. (Be sure to update paths to the H2 jar and the rufus directory below).

```
$ java -cp ~/bin/h2/bin/h2*.jar org.h2.tools.Shell -url "jdbc:h2:~/path_to_rufus/target/rufusdb" -driver "org.h2.Driver" -user "" -password ""

Welcome to H2 Shell 1.4.196 (2017-06-10)
Exit with Ctrl+C
Commands are case insensitive; SQL statements end with ';'
help or ?      Display this help
list           Toggle result list / stack trace mode
maxwidth       Set maximum column width (default is 100)
autocommit     Enable or disable autocommit
history        Show the last 20 statements
quit or exit   Close the connection and exit

sql> SHOW TABLES;

TABLE_NAME            | TABLE_SCHEMA
ARTICLES              | PUBLIC
DATABASECHANGELOG     | PUBLIC
DATABASECHANGELOGLOCK | PUBLIC
PUBLICSOURCES         | PUBLIC
RUFUSUSER             | PUBLIC
SOURCES               | PUBLIC
(6 rows, 32 ms)

sql>
```

### API
Most endpoints return articles as HTML straight from the server for consumption on the client. However, if JSON is desired, once can simply ask for it.

as HTML

```
$ curl -i http://localhost:8080/api/articles/frontpage

HTTP/1.1 200 OK
Date: Thu, 28 Dec 2017 00:44:53 GMT
Content-Type: text/html
Vary: Accept-Encoding
Transfer-Encoding: chunked

<div class="eight columns" id=https://www.wired.com/story/most-read-wired-business-stories-2017>
   <div class="article-content">
      <div class="title">
         <h6><a href=https://www.wired.com/story/most-read-wired-business-stories-2017>WIRED Business Stories of 2017</a></h6>
      </div>
      <div class="description">
         <p>The future of jobs weighed heavy on everyone&#39;s minds</p>
      </div>
   </div>
</div>
```
as JSON
```
$ curl -i -H "Accept: application/json" http://localhost:8080/api/articles/frontpage

HTTP/1.1 200 OK
Date: Wed, 27 Dec 2017 23:36:51 GMT
Content-Type: application/json
Vary: Accept-Encoding
Content-Length: 6503

{"articles":[{"title":"The Most-read WIRED Business Stories of 2017","publicationDate":"15:00 PM UTC - 12/27/2017","authors":["Andrea Valdez"],"description":"The future of jobs weighed heavy on everyone's minds.","url":"https://www.wired.com/story/most-read-wired-business-stories-2017","channelTitle":"Wired","channelUrl":"https://www.wired.com","bookmark":false}, ...
```

### Todos
* HTTPS.
* OPML import/export.
* Pagination of endpoints returning article collections.
* Cache investigation, i.e. is non real-time article feed updates preferable?
* Add "bookmarking" functionality on frontend. Endpoints and data services are complete on the backend, but lack the frontend access.
* Add options to add and view collection tags on the frontend. Again, endpoints and data services for the functionality is complete on the backend yet lack the client integration. 
* User management
  * password recovery.
  * username updates.
  * etc.
  * Make things generally _better_ on the frontend. I am unfortunately not a frontend developer and may have leveraged one or more js hacks than is generally advisable (:

### Bugs/ Contributing
Contributions are welcome, and would be greatly appreciated on the frontend side of things! (refer to the todos above if looking for something to tackle).

Feel free to shoot me an email about any questions or bugs that you come across @ tanzajohn@gmail.com

### License
Rufus is [MIT](https://github.com/jtanza/rufus/blob/master/LICENSE.txt) licensed.
