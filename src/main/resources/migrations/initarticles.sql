create table articles (
    userid int,
    title varchar(50),
    date timestamp with time zone,
    description varchar(150),
    url varchar(100),
    channelTitle varchar(50),
    channelUrl varchar(50),
    authors text[],
    constraint pk_url primary key(userid, url),
    constraint fk_userid foreign key(userid) references rufususer(userid)
)
