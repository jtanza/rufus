package com.tanza.rufus.api;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import com.tanza.rufus.feed.FeedParser;
import org.apache.commons.collections.CollectionUtils;

import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulation of an article source
 * post processing by {@link FeedParser}
 *
 * Created by jtanza.
 */

public class RufusFeed {
    private final URL feedUrl;
    private final SyndFeed feed;
    private final List<String> tags;

    private RufusFeed(URL feedUrl, SyndFeed feed, List<String> tags) {
        this.feedUrl = feedUrl;
        this.feed = feed;
        this.tags = tags;
    }

    public static RufusFeed generate(URL url, List<String> tags) {
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = null;
        try {
            feed = input.build(new XmlReader(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //never null
        tags = CollectionUtils.isEmpty(tags) ? Collections.EMPTY_LIST : tags;
        return new RufusFeed(url,feed, tags);
    }

    public URL getFeedUrl() {
        return feedUrl;
    }

    public SyndFeed getFeed() {
        return feed;
    }

    public List<String> getTags() {
        return tags;
    }
}

