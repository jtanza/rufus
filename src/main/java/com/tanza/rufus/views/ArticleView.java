package com.tanza.rufus.views;

import com.tanza.rufus.api.Article;
import io.dropwizard.views.View;

import java.util.Collection;
import java.util.List;

/**
 * @author jtanza
 */
public class ArticleView extends View {
    private static final String URL = "articles.mustache";

    private final Collection<Article> articles;

    public ArticleView(Collection<Article> articles) {
        super(URL);
        this.articles = articles;
    }

    public Collection<Article> getArticles() {
        return articles;
    }
}
