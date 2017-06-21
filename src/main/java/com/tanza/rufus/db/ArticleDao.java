package com.tanza.rufus.db;

import com.tanza.rufus.api.Article;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;
import java.util.Set;

/**
 * Dao for saved articles (bookmarks)
 *
 */
@RegisterMapper(ArticleMapper.class)
public interface ArticleDao {

    @SqlQuery("select * from rufususer left outer join articles on rufususer.userid = articles.userid where rufususer.userid = :id")
    Set<Article> getBookmarked(@Bind("id") int id);

    @SqlUpdate(
            "insert into articles(userid, title, date, description, url, channelTitle, channelUrl, authors)" +
            "values ((select userid from rufususer where userid = :id), :title, :date, :description, :url, :channelTitle, :channelUrl, :authors)"
    )
    void bookmarkArticle(@Bind("id") int id, @BindArticle Article article);

    @SqlUpdate("delete from articles where userid = :id and url = :url")
    void removeArticle(@Bind("id") int id, @Bind("url") String url);
}
