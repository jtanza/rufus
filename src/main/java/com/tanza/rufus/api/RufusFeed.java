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
    private final Source source;
    private final SyndFeed feed;

    private RufusFeed(Source source, SyndFeed feed) {
        this.feed = feed;
        this.source = source;
    }

    public static RufusFeed generate(Source source) {
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = null;
        URL url = source.getUrl();
        try {
            feed = input.build(new XmlReader(url));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (CollectionUtils.isEmpty(source.getTags())) {
            source.setTags(Collections.EMPTY_LIST); //never null!
        }

        return new RufusFeed(source, feed);
    }

    public Source getSource() {
        return source;
    }

    public SyndFeed getFeed() {
        return feed;
    }

}


