package com.tanza.rufus.api;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import com.tanza.rufus.feed.FeedParser;

import java.net.URL;

/**
 * Encapsulation of an article source
 * post processing by {@link FeedParser}
 *
 * Created by jtanza.
 */

public class RufusFeed {
    private URL feedUrl;
    private SyndFeed feed;

    private RufusFeed(URL feedUrl, SyndFeed feed) {
        this.feedUrl = feedUrl;
        this.feed = feed;
    }

    public static RufusFeed generate(URL url) {
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = null;
        try {
            feed = input.build(new XmlReader(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RufusFeed(url,feed);
    }

    public URL getFeedUrl() {
        return feedUrl;
    }

    public SyndFeed getFeed() {
        return feed;
    }
}

