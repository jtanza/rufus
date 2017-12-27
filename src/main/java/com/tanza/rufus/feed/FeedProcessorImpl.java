package com.tanza.rufus.feed;

import com.tanza.rufus.api.*;
import com.tanza.rufus.core.User;
import com.tanza.rufus.db.ArticleDao;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.tanza.rufus.feed.FeedConstants.PUB_USER_KEY;

public class FeedProcessorImpl implements FeedProcessor {
    private static final Logger logger = LoggerFactory.getLogger(FeedProcessorImpl.class);

    private static final int MAX_CACHE = 1_000;
    private static final int TTL = 3;

    private final ArticleDao articleDao;

    private LoadingCache<Long, Map<Channel, List<Document>>> articleCache;

    private FeedProcessorImpl(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }

    public static FeedProcessorImpl newInstance(ArticleDao articleDao) {
        FeedProcessorImpl feedProcessorImpl = new FeedProcessorImpl(articleDao);
        feedProcessorImpl.init();
        return feedProcessorImpl;
    }

    private void init() {
        articleCache = CacheBuilder.newBuilder()
            .maximumSize(MAX_CACHE)
            .expireAfterAccess(TTL, TimeUnit.MINUTES)
            .build(
                new CacheLoader<Long, Map<Channel, List<Document>>>() {
                    @Override
                    public Map<Channel, List<Document>> load(Long userId) throws Exception {
                        return buildChannelMap(userId);
                    }
                }
            );
    }

    @Override
    public List<Article> buildArticleCollection(User user) {
        long userId = user.getId();
        return articlesFromChannelMap(userId, getChannelMap(userId));
    }

    @Override
    public List<Article> buildArticleCollection() {
        return articlesFromChannelMap(PUB_USER_KEY, getChannelMap(PUB_USER_KEY));
    }

    @Override
    public List<Article> buildTagCollection(User user, String tag) {
        long userId = user.getId();
        Map<Channel, List<Document>> tagged = getTagged(getChannelMap(userId), tag);
        return articlesFromChannelMap(userId, tagged, FeedConstants.DEFAULT_DOCS_PER_FEED);
    }

    @Override
    public List<Article> buildTagCollection(String tag) {
        Map<Channel, List<Document>> tagged = getTagged(getChannelMap(PUB_USER_KEY), tag);
        return articlesFromChannelMap(PUB_USER_KEY, tagged, FeedConstants.DEFAULT_DOCS_PER_FEED);
    }

    @Override
    public List<Article> buildFrontpageCollection(User user) {
        long userId = user.getId();
        Map<Channel, List<Document>> frontPage = getFrontPage(getChannelMap(userId));
        return articlesFromChannelMap(userId, frontPage, FeedConstants.DEFAULT_DOCS_PER_FEED);
    }

    @Override
    public List<Article> buildFrontpageCollection() {
        Map<Channel, List<Document>> frontPage = getFrontPage(getChannelMap(PUB_USER_KEY));
        return articlesFromChannelMap(PUB_USER_KEY, frontPage, FeedConstants.DEFAULT_DOCS_PER_FEED);
    }

    @Override
    public void invalidateCache(long userId) {
        articleCache.invalidate(userId);
    }

    private List<Article> articlesFromChannelMap(long userId, Map<Channel, List<Document>> channelMap, int limit) {
        if (MapUtils.isEmpty(channelMap)) {
            return Collections.emptyList();
        }
        Map<Channel, List<Document>> filteredMap = new HashMap<>();
        channelMap.entrySet().forEach(e -> filteredMap.put(e.getKey(), e.getValue().stream().limit(limit).collect(Collectors.toList())));

        List<Article> articles = new ArrayList<>();
        filteredMap.entrySet().forEach(e -> articles.addAll(Article.of(e.getKey(), e.getValue().stream().collect(Collectors.toList()))));

        FeedUtils.markBookmarks(articles, articleDao.getBookmarked(userId));
        return FeedUtils.sort(articles);
    }

    private List<Article> articlesFromChannelMap(long userId, Map<Channel, List<Document>> channelMap) {
        if (MapUtils.isEmpty(channelMap)) {
            return Collections.emptyList();
        }
        List<Article> articles = new ArrayList<>();
        channelMap.entrySet().forEach(e -> articles.addAll(Article.of(e.getKey(), e.getValue().stream().collect(Collectors.toList()))));
        FeedUtils.markBookmarks(articles, articleDao.getBookmarked(userId));
        return FeedUtils.sort(articles);
    }

    private Map<Channel, List<Document>> getChannelMap(long userId) {
        Map<Channel, List<Document>> docMap;
        try {
            docMap = articleCache.get(userId);
        } catch (ExecutionException e) {
            logger.error("could not load cache for user {}, rebuilding article map..", userId);
            docMap = buildChannelMap(userId);
        }
        return docMap;
    }

    private Map<Channel, List<Document>> buildChannelMap(long userId) {
        List<Source> sources;
        if (userId == PUB_USER_KEY) {
            sources = articleDao.getPublicSources();
        } else if (articleDao.hasSubscriptions(userId)) {
            sources = articleDao.getSources(userId);
        } else {
            return Collections.emptyMap();
        }

        List<RufusFeed> requests = sources.parallelStream().map(RufusFeed::generate).collect(Collectors.toList());

        Map<Channel, List<Document>> ret = new HashMap<>();
        requests.stream().filter(r -> r.getFeed() != null).forEach(r -> {
            Pair<SyndFeed, List<SyndEntry>> synd = feedPair(r);
            ret.put(Channel.of(
                synd.getKey().getTitle(),
                synd.getKey().getLanguage(),
                synd.getKey().getLink(),
                r.getSource()),
                extractDocuments(synd));
        });

        return ret;
    }

    @SuppressWarnings("unchecked")
    private Pair<SyndFeed, List<SyndEntry>> feedPair(RufusFeed request) {
        SyndFeed feed = request.getFeed();
        return ImmutablePair.of(feed, feed.getEntries());
    }

    private static List<Document> extractDocuments(Pair<SyndFeed, List<SyndEntry>> feedEntry) {
        List<Document> ret = new ArrayList<>();
        feedEntry.getRight().forEach(e -> {
            FeedUtils.mergeAuthors(e);
            String description = e.getDescription() != null ? FeedUtils.clean(e.getDescription().getValue()) : StringUtils.EMPTY;
            if (description.length() > FeedConstants.MAX_DESCRIP_LEN) {
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

    private static Map<Channel, List<Document>> getFrontPage(Map<Channel, List<Document>> channelMap) {
        return channelMap.entrySet().stream()
            .filter(e -> e.getKey().getSource().isFrontpage())
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    private static Map<Channel, List<Document>> getTagged(Map<Channel, List<Document>> channelMap, String tag) {
        return channelMap.entrySet().stream()
            .filter(e -> e.getKey().getSource().getTags().contains(tag))
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }
}

