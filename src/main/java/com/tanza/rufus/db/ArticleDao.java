package com.tanza.rufus.db;

import com.tanza.rufus.api.Article;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by jtanza.
 */
@RegisterMapper(ArticleMapper.class)
public interface ArticleDao {

    @SqlQuery("select * from rufususer left outer join articles on rufususer.userid = articles.userid where rufususer.userid = :id")
    List<Article> getBookmarked(@Bind("id") int id);

    @SqlUpdate(
            "insert into articles(userid, title, date, description, url, channelTitle, channelUrl, authors)" +
            "values ((select userid from rufususer where userid = :id), :title, :date, :description, :url, :channelTitle, :channelUrl, :authors)"
    )
    void bookmarkArticle(@Bind("id") int id, @BindArticle Article article);
}
