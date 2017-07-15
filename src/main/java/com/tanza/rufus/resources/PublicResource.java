package com.tanza.rufus.resources;

import com.tanza.rufus.api.Article;
import com.tanza.rufus.api.Source;
import com.tanza.rufus.core.User;
import com.tanza.rufus.db.ArticleDao;
import com.tanza.rufus.db.UserDao;
import com.tanza.rufus.feed.FeedProcessor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tanza.rufus.feed.FeedConstants.DEFAULT_DOCS_PER_FEED;

/**
 * Created by jtanza.
 */
@Path("/public")
@Produces(MediaType.APPLICATION_JSON)
public class PublicResource {

    private final UserDao userDao;
    private final ArticleDao articleDao;
    private final FeedProcessor processor;

    public PublicResource(UserDao userDao, ArticleDao articleDao, FeedProcessor processor) {
        this.userDao = userDao;
        this.articleDao = articleDao;
        this.processor = processor;
    }

    @Path("/frontpage")
    @GET
    public Response frontPage() {
        List<Article> articles = processor.buildFrontpageCollection(userDao.getPublicUser(), DEFAULT_DOCS_PER_FEED);
        return Response.ok(articles).build();
    }

    @Path("/all")
    @GET
    public Response all() {
        List<Article> articles = processor.buildArticleCollection(userDao.getPublicUser());
        return Response.ok(articles).build();
    }

    @Path("/tagged")
    @GET
    public Response byTag(@QueryParam("tag") String tag) {
        List<Article> articles = processor.buildTagCollection(userDao.getPublicUser(), tag, DEFAULT_DOCS_PER_FEED);
        return Response.ok(articles).build();
    }

    @Path("/tagStubs")
    @GET
    public Response tagStubs() {
        User u = userDao.getPublicUser();
        Set<String> tags = articleDao.getSources(u.getId()).stream()
                .map(Source::getTags)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toSet());

        return Response.ok(tags).build();
    }
}
