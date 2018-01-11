package com.tanza.rufus.api;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Article implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(Article.class);

    private String title;
    private Date publicationDate;
    private List authors;
    private String description;
    private String url;
    private String channelTitle;
    private String channelUrl;
    private boolean bookmark;

    public Article() {}

    public Article(String title, Date publicationDate, List authors, String description, String url, String channelTitle, String channelUrl) {
        this.title = Objects.requireNonNull(title, "title must not be null!");
        this.publicationDate = Objects.requireNonNull(publicationDate, "pub date must not be null!");
        this.authors = Objects.requireNonNull(authors, "authors must not be null");
        this.description = Objects.requireNonNull(description, "description must not be null");
        this.url = Objects.requireNonNull(url, "url must not ne null");
        this.channelTitle = Objects.requireNonNull(channelTitle, "channel title must not ne null");
        this.channelUrl = Objects.requireNonNull(channelUrl, "channel url must not be null");
    }

    public static List<Article> of(Channel c, Collection<Document> documents) {
        List<Article> articles = new ArrayList<>();
        documents.forEach(d -> {
            try {
                articles.add(new Article(d.getTitle(), d.getPublicationDate(), d.getAuthors(), d.getDescription(), d.getUrl(), d.getChannelTitle(), c.getUrl()));
            } catch (NullPointerException e) {
                logger.debug("null value for document {}, discarding... {}", c.getTitle(), e);
            }
        });
        return articles;
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

    public boolean isBookmark() {
        return bookmark;
    }

    public void setBookmark(boolean bookmark) {
        this.bookmark = bookmark;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Article)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        Article article = (Article) o;
        return new EqualsBuilder()
                .append(title, article.getTitle())
                .append(channelUrl, article.getChannelUrl())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(13, 23)
                .append(title)
                .append(channelUrl)
                .toHashCode();
    }
}

