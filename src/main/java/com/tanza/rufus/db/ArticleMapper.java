package com.tanza.rufus.db;

import com.tanza.rufus.api.Article;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by jtanza.
 */
public class ArticleMapper implements ResultSetMapper<Article>{

    @Override
    public Article map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {

        Array articles = resultSet.getArray("authors");
        if (articles == null) {
            //TODO refactor to return optional
           throw new RuntimeException("No articles bookmarked!");
        }

        Date date = new Date(resultSet.getTimestamp("date").getTime());
        List<String> authors = Arrays.asList((String[]) articles.getArray());

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
