package com.tanza.rufus.resources;

import com.tanza.rufus.api.Article;
import com.tanza.rufus.api.Source;
import com.tanza.rufus.core.User;
import com.tanza.rufus.db.ArticleDao;
import com.tanza.rufus.db.UserDao;
import com.tanza.rufus.feed.FeedParser;
import com.tanza.rufus.feed.FeedProcessor;
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
    public ArticleView frontPage(@Auth User user) {
        List<Article> articles = processor.buildFrontpageCollection(user, DEFAULT_DOCS_PER_FEED);
        return new ArticleView(articles);
    }

    @Path("/all")
    @Produces(MediaType.TEXT_HTML)
    @GET
    public ArticleView all(@Auth User user) {
        List<Article> articles = processor.buildArticleCollection(user);
        return new ArticleView(articles);
    }

    @Path("/tagged")
    @GET
    public Response byTag(@Auth User user, @QueryParam("tag") String tag) {
        List<Article> articles = processor.buildTagCollection(user, tag, DEFAULT_DOCS_PER_FEED);
        return Response.ok(articles).build();
    }

    @Path("/tagStubs")
    @GET
    public Response tagStubs(@Auth User user) {
        User u = userDao.findByEmail(user.getEmail());
        Set<String> tags = articleDao.getSources(u.getId()).stream()
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
        User u = userDao.findByEmail(user.getEmail());
        if (articleDao.getBookmarked(u.getId()).contains(article)) throw new BadRequestException("Article is already bookmarked!");
        articleDao.bookmarkArticle(u.getId(), article);
        return Response.ok().build();
    }

    @Path("/isBookmarked")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public Response isBookmarked(@Auth User user, Article article) {
        User u = userDao.findByEmail(user.getEmail());
        boolean isBookmarked = articleDao.getBookmarked(u.getId()).contains(article);
        return Response.ok(isBookmarked).build();
    }

    @Path("/removeBookmark")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public Response removeBookmark(@Auth User user, Article article) {
        User u = userDao.findByEmail(user.getEmail());
        articleDao.removeArticle(u.getId(), article.getUrl());
        return Response.ok().build();
    }

    @Path("/bookmarked")
    @GET
    public Response bookmarked(@Auth User user) {
        User u = userDao.findByEmail(user.getEmail());
        Set<Article> bookmarked = articleDao.getBookmarked(u.getId());
        return Response.ok(bookmarked).build();
    }

    @Path("/new")
    @POST
    public Response addFeed(@Auth User user, List<String> feeds) {
        if (feeds.isEmpty()) throw new BadRequestException();
        return Response.ok(parser.parse(user, feeds)).build();
    }

    @Path("/frontpageNew")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public Response addFrontpage(@Auth User user, Source source) {
        if (StringUtils.isBlank(source.getUrl().toString())) throw new BadRequestException();
        User u = userDao.findByEmail(user.getEmail());
        articleDao.removeFrontpage(u.getId(), source);
        return Response.ok().build();
    }

    @Path("sources")
    @GET
    public Response getSources(@Auth User user) {
        User u = userDao.findByEmail(user.getEmail());
        return Response.ok(articleDao.getSources(u.getId())).build();
    }

    private class MessageWrapper {
        private String message;

        public MessageWrapper() {} //dummy jackson constructor

        public MessageWrapper(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
