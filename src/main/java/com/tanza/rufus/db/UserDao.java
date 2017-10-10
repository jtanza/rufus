package com.tanza.rufus.db;

import com.tanza.rufus.core.User;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by jtanza.
 */
public interface UserDao {

    @RegisterMapper(UserMapper.class)
    @SqlQuery("select * from rufususer")
    List<User> getAll();

    @RegisterMapper(UserMapper.class)
    @SqlQuery("select * from rufususer where EMAIL = :email")
    User findByEmail(@Bind("email") String email);

    @SqlUpdate("insert into rufususer (ID, EMAIL) values (:id, :email)")
    void addUser(@BindBean User user);
}
