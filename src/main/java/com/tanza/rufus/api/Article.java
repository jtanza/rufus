package com.tanza.rufus.api;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author jtanza
 */
public class Article {

    private String title;
    private Date publicationDate;
    private List authors;
    private String description;
    private String url;
    private String channelTitle;
    private String channelUrl;

    public Article(String title, Date publicationDate, List authors, String description, String url, String channelTitle, String channelUrl) {
        this.title = Objects.requireNonNull(title, "title must not be null!");
        this.publicationDate = Objects.requireNonNull(publicationDate, "pub date must not be null!");
        this.authors = Objects.requireNonNull(authors, "authors must not be null");
        this.description = Objects.requireNonNull(description, "description must not be null");
        this.url = Objects.requireNonNull(url, "url must not ne null");
        this.channelTitle = Objects.requireNonNull(channelTitle, "channel title must not ne null");
        this.channelUrl = Objects.requireNonNull(channelUrl, "channel url must not be null");
    }

    public static Article of(Channel c, Document d) {
        return new Article(d.getTitle(), d.getPublicationDate(), d.getAuthors(), d.getDescription(), d.getUrl(), d.getChannelTitle(), c.getUrl());
    }

    public String getTitle() {
        return title;
    }

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="HH:mm a z - MM/dd/yyyy")
    public Date getPublicationDate() {
        return publicationDate;
    }

    public List getAuthors() {
        return authors;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public String getChannelUrl() {
        return channelUrl;
    }

    public static Comparator<Article> byDate = Comparator.comparing(Article::getPublicationDate);
}

