package com.tanza.rufus.db;

import com.tanza.rufus.api.Article;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jtanza.
 */
public class ArticleMapper implements ResultSetMapper<Article>{

    @Override
    public Article map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {

        Array articles = resultSet.getArray("authors");
        if (articles == null) {
            //TODO this probably shouldnt throw a re
            //should probably be an optional
           throw new RuntimeException("No articles bookmarked!");
        }

        List<String> asList = Arrays.asList((String[]) articles.getArray());
        return new Article (
                resultSet.getString("title"),
                resultSet.getDate("date"),
                asList,
                resultSet.getString("description"),
                resultSet.getString("url"),
                resultSet.getString("channelTitle"),
                resultSet.getString("channelUrl")
        );
    }
}
