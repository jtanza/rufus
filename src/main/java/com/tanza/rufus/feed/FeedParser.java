package com.tanza.rufus.feed;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.apache.commons.validator.routines.UrlValidator;

import java.net.URL;
import java.util.Collections;

/**
 * Created by jtanza.
 */

public class FeedParser {
    private static final String[] SCHEMES = {"http", "https"};

    private boolean valid;
    private ValidationError error;
    private URL url;

    private FeedParser(boolean valid, ValidationError error, URL url) {
        this.valid = valid;
        this.error = error;
        this.url = url;
    }

    private static FeedParser valid(URL url) {
        return new FeedParser(true, null, url);
    }

    private static FeedParser invalid(ValidationError error) {
        return new FeedParser(false, error, null);
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
            //TODO finish me
            return FeedParser.invalid(ValidationError.of("Could not parse URL!"));
        }

        try {
            URL url = new URL(feedRequestUrl);
            SyndFeedInput input = new SyndFeedInput();
            //ensure request is a valid rss feed
            //does this throw an error?
            SyndFeed feed = input.build(new XmlReader(url));
            return FeedParser.valid(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //TODO finish me
        return FeedParser.invalid(ValidationError.of("Could not parse URL!"));
    }

    public boolean isValid() {
        return valid;
    }

    public ValidationError getError() {
        return error;
    }

    public URL getUrl() {
        return url;
    }

    public static class ValidationError {

        private String message;

        private ValidationError(String message) {
            this.message = message;
        }

        public static ValidationError of(String message) {
            return new ValidationError(message);
        }

        public String getMessage() {
            return message;
        }
    }
}

