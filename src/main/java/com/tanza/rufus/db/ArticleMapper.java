package com.tanza.rufus.db;

import com.tanza.rufus.api.Article;

import org.apache.commons.lang3.StringUtils;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by jtanza.
 */
public class ArticleMapper implements ResultSetMapper<Article> {

    @Override
    public Article map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        String title = resultSet.getString("title");
        if (StringUtils.isEmpty(title)) {
            return null;
        }

        Article article = new Article (
            title,
                new Date(resultSet.getTimestamp("date").getTime()),
                Arrays.asList((String[]) resultSet.getArray("authors").getArray()),
                resultSet.getString("description"),
                resultSet.getString("url"),
                resultSet.getString("channelTitle"),
                resultSet.getString("channelUrl")
        );
        article.setBookmark(true); //saved articles are always bookmarked articles
        return article;
    }
}
