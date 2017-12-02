package com.tanza.rufus.api;

import com.sun.syndication.feed.synd.SyndEntry;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Encapsulation of meta data pulled from a {@link SyndEntry}.
 * Contains data about an article from an RSS feed source.
 *
 * Created by jtanza.
 */
public class Document {
    private String title;
    private Date publicationDate;
    private List authors;
    private String description;
    private String url;
    private String channelTitle;

    private Document(String title, Date publicationDate, List authors, String description, String url, String channelTitle) {
        this.title = Objects.requireNonNull(title, "title must not be null!");
        this.publicationDate = Objects.requireNonNull(publicationDate, "pub date must not be null!");
        this.authors = Objects.requireNonNull(authors, "authors must not be null");
        this.description = Objects.requireNonNull(description, "description must not be null");
        this.url = Objects.requireNonNull(url, "url must not ne null");
        this.channelTitle = Objects.requireNonNull(channelTitle, "channel title must not ne null");
    }

    public static Document of(String title, Date publicationDate, List authors, String description, String link, String channelTitle) {
        return new Document(title, publicationDate, authors, description, link, channelTitle);
    }

    public String getTitle() {
        return title;
    }

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
}
