package com.tanza.rufus.feed;

import com.google.common.cache.LoadingCache;
import com.tanza.rufus.api.Article;
import com.tanza.rufus.api.Source;
import com.tanza.rufus.core.User;

import java.util.List;

/**
 * Service responsible for building/aggregating collections
 * of {@link Article}s from the set of {@link Source}s a {@link User} is currently subscribed to.
 *
 * @author jtanza
 */
public interface FeedProcessor {

    /**
     * Loads all {@link Article}s a {@link User} may have
     * access to within the system.
     *
     * @param user
     * @return
     */
    List<Article> buildArticleCollection(User user);

    /**
     * Loads all public {@link Article}s within the system, i.e.
     * {@link Article}s which are used to populate the client on
     * anonymous sessions.
     *
     * @return
     */
    List<Article> buildArticleCollection();

    /**
     * Loads {@link Article}s from {@link Source}s
     * matching the requested {@param tag}.
     *
     * @param user
     * @param tag
     * @param docsPerChannel
     * @return
     */
    List<Article> buildTagCollection(User user, String tag, int docsPerChannel);

    /**
     * Loads public {@link Article}s from public {@link Source}s
     * matching the requested {@param tag}.
     *
     * @param tag
     * @param docsPerChannel
     * @return
     */
    List<Article> buildTagCollection(String tag, int docsPerChannel);

    /**
     * Generates a collection of public {@link Article}s which have
     * been denoted for display on the "Front Page" of the client.
     *
     * @param user
     * @param docsPerChannel
     * @return
     */
    List<Article> buildFrontpageCollection(User user, int docsPerChannel);

    /**
     * Generates a collection of public {@link Article}s which have
     * been denoted for display on the "Front Page" of the client.
     *
     * @param docsPerChannel
     * @return
     */
    List<Article> buildFrontpageCollection(int docsPerChannel);

    /**
     * Immediately invalidates an {@link User}s current standing {@link LoadingCache} containing
     * their current collection of cached articles.
     *
     * @param userId
     */
    void invalidateCache(long userId);
}
