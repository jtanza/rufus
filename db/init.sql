create table feed (
  id        integer primary key asc,
  uuid      text unique not null,
  title     text not null,
  link      text not null,
  frontpage integer not null default 1,
  created   datetime default current_timestamp
);

create index feed_uuid_idx on feed(uuid);

-- sqlite> .read db/init.sql
-- insert into feed(uuid, title, link) values (hex(randomblob(16)), 'NYT > World News', 'https://rss.nytimes.com/services/xml/rss/nyt/World.xml'), (hex(randomblob(16)), 'The Marginalian', 'https://feeds.feedburner.com/brainpickings/rss');

create table article (
  id          integer primary key asc,
  uuid        text unique not null,
  feedid      integer not null,
  link        text not null,
  title       text,
  description text,
  content     text,
  published   text,
  authors     text,
  expiration datetime default (unixepoch('now', '+1 hour')),
  foreign key(feedid) references feed(id)
);

create index article_uuid_idx on article(uuid);
create index article_feed_idx on article(feedid);
create index article_expiration_idx on article(expiration);