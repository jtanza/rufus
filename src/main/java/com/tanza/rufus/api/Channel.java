package com.tanza.rufus.api;

import java.util.List;

/**
 * @author jtanza
 */
public class Channel {
    private final String title;
    private final String language;
    private final String url;
    private final List<String> tags;

    private Channel(String title, String language, String url, List<String> tags) {
        this.title = title;
        this.language = language;
        this.url = url;
        this.tags = tags;
    }

    public static Channel of(String title, String language, String url, List<String> tags) {
        return new Channel(title, language, url, tags);
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

    public List<String> getTags() {
        return tags;
    }
}

