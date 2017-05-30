create table sources (
    userid int,
    source varchar(200),
    frontpage boolean default false,
    tags text[],
    constraint pk_sources primary key(userid, source),
    constraint fk_userid foreign key(userid) references rufususer(userid)
)