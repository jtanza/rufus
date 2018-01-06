package com.tanza.rufus.db;

import com.tanza.rufus.api.Article;
import com.tanza.rufus.api.Source;
import com.tanza.rufus.core.User;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;
import java.util.Set;

/**
 * DAO for saved {@link Article}s.
 *
 * All {@link Article}s persisted to disk are those
 * in which an {@link User} has bookmarked on the client.
 *
 */
@RegisterMapper(ArticleMapper.class)
public interface ArticleDao {

    @SqlQuery("select * from rufususer left outer join articles on rufususer.userid = articles.userid where rufususer.userid = :id")
    Set<Article> getBookmarked(@Bind("id") long id);

    @SqlUpdate(
            "insert into articles(userid, title, date, description, url, channelTitle, channelUrl, authors)" +
            "values ((select userid from rufususer where userid = :id), :title, :date, :description, :url, :channelTitle, :channelUrl, :authors)"
    )
    void bookmarkArticle(@Bind("id") long id, @BindArticle Article article);

    @SqlUpdate("delete from articles where userid = :id and url = :url")
    void removeArticle(@Bind("id") long id, @Bind("url") String url);

    @RegisterMapper(SourceMapper.class)
    @SqlQuery("select * from rufususer left outer join sources on rufususer.userid = sources.userid where rufususer.userid = :id")
    List<Source> getSources(@Bind("id") long id);

    @RegisterMapper(SourceMapper.class)
    @SqlQuery("select * from public_sources")
    List<Source> getPublicSources();

    @SqlUpdate("insert into sources(userid, source) values((select userid from rufususer where userid = :id), :source)")
    void addSource(@Bind("id") long id, @Bind("source") String source);

    @SqlUpdate("insert into sources(userid, source, frontpage) values((select userid from rufususer where userid = :id), :source, true)")
    void addFrontpageSource(@Bind("id") long id, @Bind("source") String source);

    @SqlUpdate("delete from sources where source = :source")
    void removeSource(@Bind("source") String source);

    @RegisterMapper(SourceMapper.class)
    @SqlQuery("select * from sources where :tag = any (tags) and userid = :id")
    List<Source> getSourcesByTag(@Bind("id") long id, @Bind("tag") String tag);

    @SqlUpdate("update sources set frontpage = TRUE where source = :source")
    void setFrontpage(@Bind("id") long id, @BindSource Source source);

    @SqlUpdate("update sources set frontpage = FALSE where source = :source")
    void removeFrontpage(@Bind("id") long id, @BindSource Source source);

    @SqlQuery("select count(source) > 0 from rufususer left outer join sources on rufususer.userid = sources.userid where rufususer.userid = :id")
    boolean hasSubscriptions(@Bind("id") long id);
}
