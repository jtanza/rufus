package com.tanza.rufus.feed;

import com.tanza.rufus.api.*;
import com.tanza.rufus.core.User;
import com.tanza.rufus.db.ArticleDao;

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
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author jtanza
 */
public class FeedProcessor {
    private static final Logger logger = LoggerFactory.getLogger(FeedProcessor.class);

    private static final long PUB_USER_KEY = Long.MIN_VALUE;
    private static final int MAX_CACHE = 1_000;
    private static final int TTL = 3;

    private final ArticleDao articleDao;

    private LoadingCache<Long, Map<Channel, List<Document>>> articleCache;

    private FeedProcessor(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }

    public static FeedProcessor newInstance(ArticleDao articleDao) {
        FeedProcessor feedProcessor = new FeedProcessor(articleDao);
        feedProcessor.init();
        return feedProcessor;
    }

    public LoadingCache<Long, Map<Channel, List<Document>>> getArticleCache() {
        return articleCache;
    }

    public void init() {
        articleCache = CacheBuilder.newBuilder()
                .maximumSize(MAX_CACHE)
                .expireAfterAccess(TTL, TimeUnit.MINUTES)
                .build(
                        new CacheLoader<Long, Map<Channel, List<Document>>>() {
                            @Override
                            public Map<Channel, List<Document>> load(Long userId) throws Exception {
                                return getCollection(userId);
                            }
                        }
                );
    }

    /**
     * Loads all {@link Article}s a {@link User} may have
     * access to within the system.
     *
     * @param user
     * @return
     */
    public List<Article> buildArticleCollection(User user) {
        long userId = user.getId();
        return loadArticles(userId, getChannelMap(userId), false, 0);
    }

    /**
     * Loads all public {@link Article}s within the system, i.e.
     * {@link Article}s which are used to populate the client on
     * anonymous sessions.
     *
     * See {@link com.tanza.rufus.resources.PublicResource}
     *
     * @return
     */
    public List<Article> buildArticleCollection() {
        return loadArticles(PUB_USER_KEY, getChannelMap(PUB_USER_KEY), false, 0);
    }

    /**
     * Loads {@link Article}s from {@link Source}s
     * matching the requested {@param tag}.
     *
     * @param user
     * @param tag
     * @param docsPerChannel
     * @return
     */
    public List<Article> buildTagCollection(User user, String tag, int docsPerChannel) {
        long userId = user.getId();
        Map<Channel, List<Document>> tagged =
            getChannelMap(userId).entrySet().stream()
                .filter(e -> e.getKey().getSource().getTags().contains(tag))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        return loadArticles(userId, tagged, true, docsPerChannel);
    }

    /**
     * Loads public {@link Article}s from public {@link Source}s
     * matching the requested {@param tag}.
     *
     * @param tag
     * @param docsPerChannel
     * @return
     */
    public List<Article> buildTagCollection(String tag, int docsPerChannel) {
        Map<Channel, List<Document>> tagged =
            getChannelMap(PUB_USER_KEY).entrySet().stream()
                .filter(e -> e.getKey().getSource().getTags().contains(tag))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        return loadArticles(PUB_USER_KEY, tagged, true, docsPerChannel);
    }

    /**
     * Generates a collection of public {@link Article}s which have
     * been denoted for display on the "Front Page" of the client.
     *
     * @param user
     * @param docsPerChannel
     * @return
     */
    public List<Article> buildFrontpageCollection(User user, int docsPerChannel) {
        long userId= user.getId();
        Map<Channel, List<Document>> tagged =
            getChannelMap(userId).entrySet().stream()
                .filter(e -> e.getKey().getSource().isFrontpage())
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        return loadArticles(userId, tagged, true, docsPerChannel);
    }


    /**
     * Generates a collection of public {@link Article}s which have
     * been denoted for display on the "Front Page" of the client.
     *
     * @param docsPerChannel
     * @return
     */
    public List<Article> buildFrontpageCollection(int docsPerChannel) {
        Map<Channel, List<Document>> tagged = getChannelMap(PUB_USER_KEY).entrySet().stream()
            .filter(e -> e.getKey().getSource().isFrontpage())
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        return loadArticles(PUB_USER_KEY, tagged, true, docsPerChannel);
    }

    private List<Article> loadArticles(long userId, Map<Channel, List<Document>> channelMap, boolean limit, int docsPerChannel) {
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

        FeedUtils.markBookmarks(articles, bookmarks);
        return FeedUtils.sort(articles);
    }

    private Map<Channel, List<Document>> getChannelMap(long userId) {
        Map<Channel, List<Document>> docMap;
        try {
            docMap = articleCache.get(userId);
        } catch (ExecutionException e) {
            logger.error("could not load cache for user {}, loading articles..", userId);
            docMap = getCollection(userId);
        }
        return docMap;
    }

    private Map<Channel, List<Document>> getCollection(long userId) {
        Map<Channel, List<Document>> ret = new ConcurrentHashMap<>();
        List<RufusFeed> requests;

        if (userId == PUB_USER_KEY) {
            requests = articleDao.getPublicSources()
                .stream().collect(Collectors.toList())
                .stream().map(RufusFeed::generate).collect(Collectors.toList());

        } else {
            requests = articleDao.getSources(userId)
                .stream().collect(Collectors.toList())
                .stream().map(RufusFeed::generate).collect(Collectors.toList());
        }

        requests.forEach(r -> {
            Pair<SyndFeed, List<SyndEntry>> synd =  buildFeedPair(r);
            ret.put(Channel.of(
                synd.getKey().getTitle(),
                synd.getKey().getLanguage(),
                synd.getKey().getLink(),
                r.getSource()),
                extractDocuments(synd, true));
        });
        return ret;
    }

    @SuppressWarnings("unchecked")
    private Pair<SyndFeed, List<SyndEntry>> buildFeedPair(RufusFeed request) {
        SyndFeed feed = request.getFeed();
        return ImmutablePair.of(feed, feed.getEntries());
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

