package com.tanza.rufus.views;

import com.tanza.rufus.api.Article;
import io.dropwizard.views.View;

import java.util.List;

/**
 * @author jtanza
 */
public class ArticleView extends View {
    private static final String URL = "articles.mustache";

    private final List<Article> articles;

    public ArticleView(List<Article> articles) {
        super(URL);
        this.articles = articles;
    }

    public List<Article> getArticles() {
        return articles;
    }
}
