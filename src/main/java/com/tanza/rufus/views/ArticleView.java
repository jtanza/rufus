package com.tanza.rufus.views;

import com.tanza.rufus.api.Article;

import io.dropwizard.views.View;

import java.util.Collection;
import java.util.Collections;

/**
 * @author jtanza
 */
public class ArticleView extends View {

    private static final String CONTENT_URL = "articles.mustache";
    private static final String EMPTY_URL = "articles_empty.mustache";

    public static final ArticleView EMPTY = new ArticleView(Collections.emptyList(), EMPTY_URL);

    private final Collection<Article> articles;

    private ArticleView(Collection<Article> articles, String URL) {
        super(URL);
        this.articles = articles;
    }

    public static ArticleView ofArticles(Collection<Article> articles) {
        return new ArticleView(articles, CONTENT_URL);
    }

    public Collection<Article> getArticles() {
        return articles;
    }
}
