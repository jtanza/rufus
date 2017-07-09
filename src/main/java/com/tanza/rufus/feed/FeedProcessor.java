package com.tanza.rufus.feed;

import com.tanza.rufus.api.*;
import com.tanza.rufus.core.User;
import com.tanza.rufus.db.ArticleDao;
import com.tanza.rufus.db.UserDao;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author jtanza
 */
public class FeedProcessor {
    private static final Logger logger = LoggerFactory.getLogger(FeedProcessor.class);

    private static final int MAX_CACHE = 10_000;
    private static final int TTL = 3;

    private final UserDao userDao;
    private final ArticleDao articleDao;

    private LoadingCache<Integer, Map<Channel, List<Document>>> articleCache;

    private FeedProcessor(UserDao userDao, ArticleDao articleDao) {
        this.userDao = userDao;
        this.articleDao = articleDao;
    }

    public static FeedProcessor newInstance(UserDao userDao, ArticleDao articleDao) {
        FeedProcessor feedProcessor = new FeedProcessor(userDao, articleDao);
        feedProcessor.init();
        return feedProcessor;
    }

    public LoadingCache<Integer, Map<Channel, List<Document>>> getArticleCache() {
        return articleCache;
    }

    public void init() {
        articleCache = CacheBuilder.newBuilder()
                .maximumSize(MAX_CACHE)
                .expireAfterAccess(TTL, TimeUnit.MINUTES)
                .build(
                        new CacheLoader<Integer, Map<Channel, List<Document>>>() {
                            @Override
                            public Map<Channel, List<Document>> load(Integer userId) throws Exception {
                                return getCollection(userId);
                            }
                        }
                );
    }

    public List<Article> buildArticleCollection(User user) {
        return loadArticles(user, getChannels(user), false, 0);
    }

    public List<Article> buildArticleCollection(User user, int docsPerChannel) {
        return loadArticles(user, getChannels(user), true, docsPerChannel);
    }

    public List<Article> buildTagCollection(User user, String tag, int docsPerChannel) {
        Map<Channel, List<Document>> tagged = getChannels(user).entrySet().stream()
                .filter(e -> e.getKey().getSource().getTags().contains(tag))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

        return loadArticles(user, tagged, true, docsPerChannel);
    }

    public List<Article> buildFrontpageCollection(User user, int docsPerChannel) {
        Map<Channel, List<Document>> tagged = getChannels(user).entrySet().stream()
                .filter(e -> e.getKey().getSource().isFrontpage())
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

        return loadArticles(user, tagged, true, docsPerChannel);
    }

    private Map<Channel, List<Document>> getChannels(User user) {
        int userId = user.getId();
        Map<Channel, List<Document>> docMap;
        try {
            docMap = articleCache.get(userId);
        } catch (ExecutionException e) {
            logger.error("could not load cache for user {}, loading articles..", userId);
            docMap = getCollection(userId);
        }
        return docMap;
    }

    private List<Article> loadArticles(User user, Map<Channel, List<Document>> channelMap, boolean limit, int docsPerChannel) {
        int userId = user.getId();

        List<Article> articles = new ArrayList<>();
        Set<Article> bookmarks = articleDao.getBookmarked(userId);

        if (limit) {
            channelMap.entrySet().forEach(e -> articles.addAll(e.getValue().stream()
                    .limit(docsPerChannel)
                    .map(d -> Article.of(e.getKey(), d))
                    .collect(Collectors.toList())));
        } else {
            channelMap.entrySet().forEach(e -> articles.addAll(e.getValue().stream()
                    .map(d -> Article.of(e.getKey(), d))
                    .collect(Collectors.toList())));
        }

        syncBookmarks(articles, bookmarks);
        return FeedUtils.sort(articles);
    }

    private Map<Channel, List<Document>> getCollection(int userId) {
        List<Source> sources = userDao.getSources(userId).stream().collect(Collectors.toList());
        List<RufusFeed> requests = FeedUtils.sourceToFeed(sources);

        return buildChannelMap(requests);
    }

    @SuppressWarnings("unchecked")
    private Pair<SyndFeed, List<SyndEntry>> generateFeedPair(RufusFeed request) {
        SyndFeed feed = request.getFeed();
        return ImmutablePair.of(feed, feed.getEntries());
    }

    private Map<Channel, List<Document>> buildChannelMap(List<RufusFeed> requests) {
        Map<Channel, List<Document>> ret = new HashMap<>();
        requests.forEach(r -> {
            Pair<SyndFeed, List<SyndEntry>> synd = generateFeedPair(r);
            ret.put(Channel.of(synd.getKey().getTitle(), synd.getKey().getLanguage(), synd.getKey().getLink(), r.getSource()), extractDocuments(synd, true));
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

    private void syncBookmarks(List<Article> articles, Set<Article> bookmarks) {
        articles.stream().filter(bookmarks::contains).forEach(a -> a.setBookmark(true));
    }
}

