package com.tanza.rufus.api;

/**
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

