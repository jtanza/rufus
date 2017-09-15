package com.tanza.rufus.resources;

import com.tanza.rufus.api.Article;
import com.tanza.rufus.api.Source;
import com.tanza.rufus.core.User;
import com.tanza.rufus.db.ArticleDao;
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
 * Controller responsible for providing unauthenticated access
 * to Public {@link Article}s, i.e. {@link Article}s which are presented
 * to unauthenticated {@link User}s within the web application.
 *
 * Created by jtanza.
 */
@Path("/public")
@Produces(MediaType.APPLICATION_JSON)
public class PublicResource {

    private final ArticleDao articleDao;
    private final FeedProcessor processor;

    public PublicResource(ArticleDao articleDao, FeedProcessor processor) {
        this.articleDao = articleDao;
        this.processor = processor;
    }

    @Path("/frontpage")
    @Produces(MediaType.TEXT_HTML)
    @GET
    public Response frontPage() {
        return ArticleResource.buildArticles(processor.buildFrontpageCollection(DEFAULT_DOCS_PER_FEED));
    }

    @Path("/all")
    @Produces(MediaType.TEXT_HTML)
    @GET
    public Response all() {
        return ArticleResource.buildArticles(processor.buildArticleCollection());
    }

    @Path("/tagged")
    @Produces(MediaType.TEXT_HTML)
    @GET
    public Response byTag(@QueryParam("tag") String tag) {
        return ArticleResource.buildArticles(processor.buildTagCollection(tag, DEFAULT_DOCS_PER_FEED));
    }

    @Path("/tagStubs")
    @GET
    public Response tagStubs() {
        Set<String> tags = articleDao.getPublicSources()
            .stream()
            .map(Source::getTags)
            .filter(Objects::nonNull)
            .flatMap(List::stream)
            .collect(Collectors.toSet());
        return Response.ok(tags).build();
    }
}
