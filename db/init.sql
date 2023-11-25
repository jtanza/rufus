create table feed (
  id        integer primary key asc,
  uuid      text unique not null,
  link      text not null,
  frontpage integer not null default 1
  created   datetime default current_timestamp
);

create index feed_uuid_idx on feed(uuid);

create table article (
  id         integer primary key asc,
  uuid       text unique not null,
  feedid     integer not null,
  link       text not null,
  expiration datetime default unixepoch('now', '+1 hour'),
  foreign key(creatorid) references user(id)
);

create index article_uuid_idx on article(uuid);
create index article_feed_idx on article(feedid);
create index article_expiration_idx on article(expiration);