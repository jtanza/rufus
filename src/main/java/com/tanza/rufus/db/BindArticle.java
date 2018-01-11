package com.tanza.rufus.db;

import com.tanza.rufus.api.Article;

import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BinderFactory;
import org.skife.jdbi.v2.sqlobject.BindingAnnotation;

import java.lang.annotation.*;
import java.sql.Array;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created by jtanza.
 */
@BindingAnnotation(BindArticle.ArticleBinderFactory.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface BindArticle {
    class ArticleBinderFactory implements BinderFactory {
        @Override
        public Binder build(Annotation annotation) {
            return (Binder<BindArticle, Article>) (sql, bindArticle, article) -> {

                sql.bind("title", article.getTitle());
                sql.bind("date", article.getPublicationDate());
                sql.bind("description", truncate(article.getDescription(), 150));
                sql.bind("url", article.getUrl());
                sql.bind("channelTitle", truncate(article.getChannelTitle(), 50));
                sql.bind("channelUrl", article.getChannelUrl());

                Array array = null;
                try {
                    array = sql.getContext().getConnection().createArrayOf("text", article.getAuthors().toArray());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sql.bindBySqlType("authors", array, Types.ARRAY);
            };
        }

        private static String truncate(String s, int n) {
            return s.substring(0, Math.min(s.length(), n));
        }
    }
}
