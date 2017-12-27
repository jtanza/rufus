package com.tanza.rufus.api;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import com.tanza.rufus.feed.FeedParser;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Collections;

/**
 * Internal representation of an RSS feed source
 * post processing by {@link FeedParser}
 *
 * Created by jtanza.
 */

public class RufusFeed {
    private static final Logger logger = LoggerFactory.getLogger(RufusFeed.class);

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
            logger.debug("Could not build SyndFeedInput for {}", url, e);
        }
        if (CollectionUtils.isEmpty(source.getTags())) {
            source.setTags(Collections.emptyList()); //never null!
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


