package com.tanza.rufus.api;

import com.sun.syndication.feed.synd.SyndFeed;

/**
 * Encapsulation of meta data pulled from a {@link SyndFeed}, used in
 * combination with a {@link Document} to generate {@link Article}s
 * on the client.
 *
 * @author jtanza
 */
public class Channel {
    private final String title;
    private final String language;
    private final String url;
    private final Source source;

    private Channel(String title, String language, String url, Source source) {
        this.title = title;
        this.language = language;
        this.url = url;
        this.source = source;
    }

    public static Channel of(String title, String language, String url, Source source ) {
        return new Channel(title, language, url, source);
    }

    public String getTitle() {
        return title;
    }

    public String getLanguage() {
        return language;
    }

    public String getUrl() {
        return url;
    }

    public Source getSource() {
        return source;
    }
}

