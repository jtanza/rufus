package com.tanza.rufus.db;

import com.tanza.rufus.api.Source;
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

    @RegisterMapper(UserMapper.class)
    @SqlQuery("select * from rufususer where email = :email and password = :password")
    User findByEmailAndPassword(@Bind("email") String email, @Bind("password") String password);

    @SqlUpdate("insert into rufususer (ID, EMAIL) values (:id, :email)")
    void addUser(@BindBean User user);

    @RegisterMapper(SourceMapper.class)
    @SqlQuery("select * from rufususer left outer join sources on rufususer.userid = sources.userid where rufususer.userid = :id")
    List<Source> getSources(@Bind("id") int id);

    @SqlUpdate("insert into sources(userid, source) values((select userid from rufususer where userid = :id), :source)")
    void addFeed(@Bind("id") int id, @Bind("source") String source);

    @RegisterMapper(SourceMapper.class)
    @SqlQuery("select * from sources where :tag = any (tags) and userid = :id")
    List<Source> getSourcesByTag(@Bind("id") int id, @Bind("tag") String tag);
}
