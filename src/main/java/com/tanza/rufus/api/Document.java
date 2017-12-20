package com.tanza.rufus.api;

import com.sun.syndication.feed.synd.SyndEntry;

import java.util.Date;
import java.util.List;

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
        this.title = title;
        this.publicationDate = publicationDate;
        this.authors = authors;
        this.description = description;
        this.url = url;
        this.channelTitle = channelTitle;
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
