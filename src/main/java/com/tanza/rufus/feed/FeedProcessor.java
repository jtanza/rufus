package com.tanza.rufus.feed;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

import com.tanza.rufus.api.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jtanza
 */
public class FeedProcessor {

    private static final Logger logger = LoggerFactory.getLogger(FeedProcessor.class);

    /**
     * @param requests
     * @return
     */
    public List<Article> buildArticleCollection(List<RufusFeed> requests) {
        return buildArticleCollection(requests, false, 0);
    }

    /**
     * @param requests
     * @return
     */
    public List<Article> buildArticleCollection(List<RufusFeed> requests, int docsPerChannel) {
        return buildArticleCollection(requests, true, docsPerChannel);
    }

    private List<Article> buildArticleCollection(List<RufusFeed> requests, boolean limit, int docsPerFeed) {
        Map<Channel, List<Document>> docMap = buildChannelMap(requests);
        List<Article> articles = new ArrayList<>();
        if (limit) {
            docMap.entrySet().forEach(e -> {
                articles.addAll(e.getValue().stream().limit(docsPerFeed).map(d -> Article.of(e.getKey(), d)).collect(Collectors.toList()));
            });
        } else {
            docMap.entrySet().forEach(e -> {
                articles.addAll(e.getValue().stream().map(d -> Article.of(e.getKey(), d)).collect(Collectors.toList()));
            });
        }
        return FeedUtils.sort(articles);
    }

    @SuppressWarnings("unchecked")
    private Pair<SyndFeed, List<SyndEntry>> generateFeedPair(RufusFeed request) {
        SyndFeed feed = request.getFeed();
        return ImmutablePair.of(feed, feed.getEntries());
    }

    private Pair<Channel, List<Document>> buildChannelPair(RufusFeed request) {
        Pair<SyndFeed, List<SyndEntry>> synd = generateFeedPair(request);
        return ImmutablePair.of(Channel.of(synd.getKey().getTitle(), synd.getKey().getLanguage(), synd.getKey().getLink()), extractDocuments(synd, true));
    }

    private Map<Channel, List<Document>> buildChannelMap(List<RufusFeed> requests) {
        Map<Channel, List<Document>> ret = new HashMap<>();
        requests.forEach(r -> {
            Pair<SyndFeed, List<SyndEntry>> synd = generateFeedPair(r);
            ret.put(Channel.of(synd.getKey().getTitle(), synd.getKey().getLanguage(), synd.getKey().getLink()), extractDocuments(synd, true));
        });
        return ret;
    }

    private static List<Document> extractDocuments(Pair<SyndFeed, List<SyndEntry>> feedEntry, boolean truncateDescriptions) {
        List<Document> ret = new ArrayList<>();
        feedEntry.getRight().forEach(e -> {
            FeedUtils.mergeAuthors(e);
            String description =  FeedUtils.clean(e.getDescription().getValue());
            if (truncateDescriptions) {
                description = FeedUtils.truncate(description, FeedConstants.MAX_DESCRIP_LEN);
            }
            ret.add(Document.of(
                    StringEscapeUtils.unescapeHtml4(e.getTitle()),
                    e.getPublishedDate(),
                    e.getAuthors(),
                    description,
                    e.getLink(),
                    feedEntry.getLeft().getTitle()
            ));
        });
        return ret;
    }
}

