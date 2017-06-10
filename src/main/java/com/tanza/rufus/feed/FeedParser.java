package com.tanza.rufus.feed;

import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.apache.commons.validator.routines.UrlValidator;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jtanza.
 */

public class FeedParser {
    private static final String[] SCHEMES = {"http", "https"};

    private boolean valid;
    private String error;
    private String url;

    private FeedParser(boolean valid, String error, String url) {
        this.valid = valid;
        this.error = error;
        this.url = url;
    }

    public static FeedParser valid(String url) {
        return new FeedParser(true, null, url);
    }

    public static FeedParser invalid(String error, String url) {
        return new FeedParser(false, error, url);
    }

    /**
     * Validates and converts @param feedRequestUrl into
     * an {@link URL}
     *
     * @param feedRequestUrl
     * @return
     */
    public static FeedParser parse(String feedRequestUrl) {
        UrlValidator urlValidator = new UrlValidator(SCHEMES);
        if (!urlValidator.isValid(feedRequestUrl)) {
            return FeedParser.invalid("Could not parse url : " + feedRequestUrl, feedRequestUrl);
        }
        try {
            URL url = new URL(feedRequestUrl);
            SyndFeedInput input = new SyndFeedInput();
            input.build(new XmlReader(url)); //ensure request is a valid rss feed
            return FeedParser.valid(feedRequestUrl);
        } catch (Exception e) {
            return FeedParser.invalid(e.getMessage(), feedRequestUrl);
        }
    }

    public boolean isValid() {
        return valid;
    }

    public String getError() {
        return error;
    }

    public String getUrl() {
        return url;
    }
}

