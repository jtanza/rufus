package com.tanza.rufus.feed;

import com.sun.syndication.feed.synd.SyndEntry;
import com.tanza.rufus.api.Article;
import com.tanza.rufus.api.RufusFeed;
import com.tanza.rufus.api.Source;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author jtanza
 */
public class FeedUtils {

    private FeedUtils() {
        throw new AssertionError(); //noninstantiability
    }

    /**
     * @param entry
     */
    public static void mergeAuthors(SyndEntry entry) {
        if (CollectionUtils.isEmpty(entry.getAuthors())) {
            entry.setAuthors(Collections.singletonList(entry.getAuthor()));
        }
    }

    /**
     * Truncate article description
     *
     * @param value
     * @param maxLength
     * @return
     */
    public static String truncate(String value, int maxLength) {
        String ret;
        ret = value.length() > maxLength ? StringUtils.abbreviate(value, maxLength) : value;
        return ret;
    }

    /**
     * @param articles
     * @return
     */
    public static List<Article> sort(List<Article> articles) {
        articles.sort((a, b) -> b.getPublicationDate().compareTo(a.getPublicationDate()));
        return articles;
    }

    public static List<RufusFeed> sourceToFeed(List<Source> sources) {
       return sources.stream().map(s -> RufusFeed.generate(s)).collect(Collectors.toList());
    }

    /**
     * Unescape html character entities and strip html tags from content
     *
     * @param content
     * @return
     */
    public static String clean(String content) {
        return Jsoup.parse(StringEscapeUtils.unescapeHtml4(content)).text();
    }

    /**
     * @param articles
     * @param bookmarks
     */
    public static void markBookmarks(List<Article> articles, Set<Article> bookmarks) {
        articles.stream().filter(bookmarks::contains).forEach(a -> a.setBookmark(true));
    }
}

