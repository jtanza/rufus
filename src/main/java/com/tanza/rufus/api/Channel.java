package com.tanza.rufus.api;

/**
 * @author jtanza
 */
public class Channel {
    private String title;
    private String language;
    private String url;

    private Channel(String title, String language, String url) {
        this.title = title;
        this.language = language;
        this.url = url;
    }

    public static Channel of(String title, String language, String url) {
        return new Channel(title, language, url);
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
}

