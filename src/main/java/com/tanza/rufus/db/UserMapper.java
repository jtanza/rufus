package com.tanza.rufus.db;

import com.tanza.rufus.core.User;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by jtanza.
 */
public class UserMapper implements ResultSetMapper<User> {
    @Override
    public User map(int index, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        User u = new User();
        u.setEmail(resultSet.getString("email"));
        u.setId(resultSet.getInt("userid"));
        u.setPassword(resultSet.getString("password"));
        return u;
    }
}
