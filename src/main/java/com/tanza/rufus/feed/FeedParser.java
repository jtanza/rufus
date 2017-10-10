package com.tanza.rufus.feed;

import com.tanza.rufus.core.User;
import com.tanza.rufus.db.ArticleDao;

import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by jtanza.
 */

public class FeedParser {
    private static final Logger logger = LoggerFactory.getLogger(FeedParser.class);

    private final ArticleDao articleDao;
    private final FeedProcessor processor;

    public FeedParser(ArticleDao articleDao, FeedProcessor processor) {
        this.articleDao = articleDao;
        this.processor = processor;
    }

    /**
     * Validates and converts {@param requestFeeds} into
     * {@link URL}s
     *
     * @param user
     * @param requestFeeds
     * @return
     */
    public List<Response> parse(User user, List<String> requestFeeds) {
        long userId = user.getId();
        Set<String> pruned = new HashSet<>(requestFeeds);
        List<String> existing = articleDao.getSources(userId).stream().map(s -> s.getUrl().toString()).collect(Collectors.toList());

        List<Response> feedResponses = new ArrayList<>();
        pruned.forEach((String f) -> {
            if (existing.contains(f)) {
                feedResponses.add(Response.invalid("Already Subscribed to Feed!", f));
            } else {
                Response parser = FeedParser.validate(f);
                if (parser.isValid()) {
                    logger.info("added feed {} for user {}", f, userId);
                    articleDao.addFeed(userId, parser.getUrl());
                }
                feedResponses.add(parser);
            }
        });
        processor.invalidateCache(userId); //update the user's article cache after having added new sources
        return feedResponses;
    }

    private static Response validate(String feedRequestUrl) {
        try {
            URL url = new URL(feedRequestUrl);
            SyndFeedInput input = new SyndFeedInput();
            input.build(new XmlReader(url)); //ensure request is a valid rss feed
            return Response.valid(feedRequestUrl);
        } catch (Exception e) {
            logger.debug("could not parse feed request {}, reason {}", feedRequestUrl, e.getMessage());
            return Response.invalid(e.getMessage(), feedRequestUrl);
        }
    }

    private static class Response {
        private boolean valid;
        private String error;
        private String url;

        private Response(boolean valid, String error, String url) {
            this.valid = valid;
            this.error = error;
            this.url = url;
        }

        public static Response valid(String url) {
            return new Response(true, null, url);
        }

        public static Response invalid(String error, String url) {
            return new Response(false, error, url);
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
}

