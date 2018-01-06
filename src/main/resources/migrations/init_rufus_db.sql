CREATE TABLE rufususer (
    userid BIGINT NOT NULL AUTO_INCREMENT,
    email TEXT NOT NULL,
    password TEXT NOT NULL,
    CONSTRAINT rufususer_pkey PRIMARY KEY (userid)
);

CREATE TABLE articles (
    userid BIGINT NOT NULL,
    title TEXT,
    date TIMESTAMP,
    description TEXT,
    url VARCHAR(500) NOT NULL,
    channeltitle TEXT,
    channelurl TEXT,
    authors ARRAY,
    CONSTRAINT pk_url PRIMARY KEY (userid, url),
    FOREIGN KEY (userid) REFERENCES rufususer(userid)
);

CREATE TABLE sources (
    userid BIGINT NOT NULL,
    source VARCHAR(500) NOT NULL,
    frontpage BOOL DEFAULT false,
    tags ARRAY,
    CONSTRAINT pk_sources PRIMARY KEY (userid, source),
    FOREIGN KEY (userid) REFERENCES rufususer(userid)
);

CREATE TABLE public_sources (
    source TEXT NOT NULL,
    frontpage BOOL DEFAULT false,
    tags ARRAY
);