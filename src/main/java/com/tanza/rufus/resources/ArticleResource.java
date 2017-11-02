package com.tanza.rufus.resources;

import com.tanza.rufus.api.Article;
import com.tanza.rufus.api.Source;
import com.tanza.rufus.core.User;
import com.tanza.rufus.db.ArticleDao;
import com.tanza.rufus.db.UserDao;
import com.tanza.rufus.feed.FeedParser;
import com.tanza.rufus.feed.FeedProcessor;
import com.tanza.rufus.feed.FeedUtils;
import com.tanza.rufus.views.ArticleView;

import io.dropwizard.auth.Auth;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.tanza.rufus.feed.FeedConstants.*;

/**
 * Created by jtanza.
 */
@Path("/articles")
@Produces(MediaType.APPLICATION_JSON)
public class ArticleResource {
    private static final Logger logger = LoggerFactory.getLogger(ArticleResource.class);

    private final UserDao userDao;
    private final ArticleDao articleDao;
    private final FeedProcessor processor;
    private final FeedParser parser;

    public ArticleResource (
        UserDao userDao,
        ArticleDao articleDao,
        FeedProcessor processor,
        FeedParser parser
    ) {
        this.userDao = userDao;
        this.articleDao = articleDao;
        this.processor = processor;
        this.parser = parser;
    }

    @Path("/frontpage")
    @Produces(MediaType.TEXT_HTML)
    @GET
    public Response frontPage(@Auth Optional<User> user) {
        if (user.isPresent()) {
            User existing = user.get();
            return articleView(() -> processor.buildFrontpageCollection(existing, DEFAULT_DOCS_PER_FEED));
        } else {
            return articleView(() -> processor.buildFrontpageCollection(DEFAULT_DOCS_PER_FEED));
        }
    }

    @Path("/all")
    @Produces(MediaType.TEXT_HTML)
    @GET
    public Response all(@Auth Optional<User> user) {
        if (user.isPresent()) {
            User existing = user.get();
            return articleView(() -> processor.buildArticleCollection(existing));
        } else {
            return articleView(processor::buildArticleCollection);
        }
    }

    @Path("/tagged")
    @Produces(MediaType.TEXT_HTML)
    @GET
    public Response byTag(@Auth Optional<User> user, @QueryParam("tag") String tag) {
        if (user.isPresent()) {
            User existing = user.get();
            return articleView(() -> processor.buildTagCollection(user.get(), tag, DEFAULT_DOCS_PER_FEED));
        } else {
            return articleView(() -> processor.buildTagCollection(tag, DEFAULT_DOCS_PER_FEED));
        }
    }

    @Path("/tagStubs")
    @GET
    public Response tagStubs(@Auth Optional<User> user) {
        List<Source> sources;
        if (user.isPresent()) {
            User present = user.get();
            if (!articleDao.hasSubscriptions(present.getId())) {
                return Response.status(Response.Status.OK)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(Collections.EMPTY_LIST)
                    .build();
            } else {
                sources = articleDao.getSources(userDao.findByEmail(present.getEmail()).getId());
            }
        } else {
            sources = articleDao.getPublicSources();
        }

        Set<String> tags = sources.stream()
                .map(Source::getTags)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toSet());

        return Response.ok(tags).build();
    }

    @Path("/bookmark")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public Response bookmark(@Auth User user, Article article) {
        user = userDao.findByEmail(user.getEmail());
        if (articleDao.getBookmarked(user.getId()).contains(article)) {
            throw new BadRequestException("Article is already bookmarked!");
        }
        articleDao.bookmarkArticle(user.getId(), article);
        return Response.ok().build();
    }

    @Path("/isBookmarked")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public Response isBookmarked(@Auth User user, Article article) {
        user = userDao.findByEmail(user.getEmail());
        return Response.ok(articleDao.getBookmarked(user.getId()).contains(article)).build();
    }

    @Path("/removeBookmark")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public Response removeBookmark(@Auth User user, Article article) {
        user = userDao.findByEmail(user.getEmail());
        articleDao.removeArticle(user.getId(), article.getUrl());
        return Response.ok().build();
    }

    @Path("/bookmarked")
    @Produces(MediaType.TEXT_HTML)
    @GET
    public Response bookmarked(@Auth User user) {
        final User u = userDao.findByEmail(user.getEmail());
        return articleView(() -> articleDao.getBookmarked(u.getId()));
    }

    @Path("/new")
    @POST
    public Response addFeed(@Auth User user, List<String> feeds) {
        if (feeds.isEmpty()) {
            throw new BadRequestException();
        }
        return Response.ok(parser.parse(user, feeds)).build();
    }

    @Path("/frontpageNew")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public Response addFrontpage(@Auth User user, Source source) {
        if (StringUtils.isBlank(source.getUrl().toString())) {
            throw new BadRequestException();
        }
        user = userDao.findByEmail(user.getEmail());
        articleDao.setFrontpage(user.getId(), source);
        return Response.ok().build();
    }

    @Path("sources")
    @GET
    public Response getSources(@Auth User user) {
        user = userDao.findByEmail(user.getEmail());
        return Response.ok(articleDao.getSources(user.getId())).build();
    }

    private Response articleView(Supplier<Collection<Article>> articleSupplier) {
        Collection<Article> articles = articleSupplier.get();
        if (articles.isEmpty() || FeedUtils.isNull(articles)) {
            return Response.ok(ArticleView.EMPTY).build();
        }
        return Response.ok(ArticleView.ofArticles(articles)).build();
    }
}
