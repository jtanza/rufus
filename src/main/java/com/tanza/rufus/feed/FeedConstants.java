package com.tanza.rufus.feed;

import com.google.common.collect.ImmutableMap;

public final class FeedConstants {
    public static final int DEFAULT_DOCS_PER_FEED = 3;
    public static final int MAX_DESCRIP_LEN = 500;
    public static final long PUB_USER_KEY = Long.MIN_VALUE;
    public static final String JWT_PROPERTY = "JWT_SECRET";
    public static final ImmutableMap<String, String> STARTER_FEEDS =
        new ImmutableMap.Builder<String, String>()
            .put("ny_times_world", "http://rss.nytimes.com/services/xml/rss/nyt/World.xml")
            .put("washington_post_politics", "http://feeds.washingtonpost.com/rss/politics")
            .put("wired", "https://www.wired.com/feed/rss")
            .put("scientific_american", "http://rss.sciam.com/ScientificAmerican-News")
            .build();
}
