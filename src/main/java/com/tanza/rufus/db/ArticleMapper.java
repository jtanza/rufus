package com.tanza.rufus.db;

import com.tanza.rufus.api.Article;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by jtanza.
 */
public class ArticleMapper implements ResultSetMapper<Article> {

    @Override
    public Article map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        Date date = new Date(resultSet.getTimestamp("date").getTime());
        List<String> authors = Arrays.asList((String[]) resultSet.getArray("authors").getArray());

        Article article = new Article (
                resultSet.getString("title"),
                date,
                authors,
                resultSet.getString("description"),
                resultSet.getString("url"),
                resultSet.getString("channelTitle"),
                resultSet.getString("channelUrl")
        );
        article.setBookmark(true); //saved articles are always bookmarked articles
        return article;
    }
}
