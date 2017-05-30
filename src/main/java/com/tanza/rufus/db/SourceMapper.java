package com.tanza.rufus.db;

import com.tanza.rufus.api.Source;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by jtanza.
 */
public class SourceMapper implements ResultSetMapper<Source> {
    @Override
    public Source map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        Source source = null;
        try {
            source = new Source(new URL(resultSet.getString("source")));
        } catch (MalformedURLException e) {
            //TODO throw error
            e.printStackTrace();
        }

        Array tags = resultSet.getArray("tags");
        List<String> asList = null;
        if (tags != null) {
            asList = Arrays.asList((String[]) tags.getArray());
        }

        source.setTags(asList);
        source.setFrontpage(resultSet.getBoolean("frontpage"));
        return source;
    }
}
