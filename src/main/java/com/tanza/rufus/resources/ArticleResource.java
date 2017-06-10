package com.tanza.rufus.resources;

import com.tanza.rufus.api.Article;
import com.tanza.rufus.api.Source;
import com.tanza.rufus.core.User;
import com.tanza.rufus.db.ArticleDao;
import com.tanza.rufus.db.UserDao;
import com.tanza.rufus.feed.FeedParser;
import com.tanza.rufus.feed.FeedProcessor;
import com.tanza.rufus.feed.FeedUtils;

import io.dropwizard.auth.Auth;

import com.codahale.metrics.annotation.Timed;

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

    private final FeedProcessor processor = new FeedProcessor();
    private final UserDao userDao;
    private final ArticleDao articleDao;

    public ArticleResource(UserDao userDao, ArticleDao articleDao) {
        this.userDao = userDao;
        this.articleDao = articleDao;
    }

    @Timed
    @Path("/frontpage")
    @GET
    public Response frontPage(@Auth User user) {
        User u = userDao.findByEmail(user.getEmail());
        List<Source> sources = userDao.getSources(u.getId()).stream().filter(Source::isFrontpage).collect(Collectors.toList());
        List<Article> articles = processor.buildArticleCollection(FeedUtils.sourceToFeed(sources), DEFAULT_DOCS_PER_FEED);
        return Response.ok(articles).build();
    }

    @Timed
    @Path("/all")
    @Produces("application/json")
    @GET
    public List<Article> all(@Auth User user) {
        User u = userDao.findByEmail(user.getEmail());
        return processor.buildArticleCollection(FeedUtils.sourceToFeed(userDao.getSources(u.getId())));
    }

    @Timed
    @Path("/tagged")
    @GET
    public Response byTag(@Auth User user, @QueryParam("tag") String tag) {
        User u = userDao.findByEmail(user.getEmail());
        List<Article> articles = processor.buildArticleCollection(FeedUtils.sourceToFeed(userDao.getSourcesByTag(u.getId(), tag)), DEFAULT_DOCS_PER_FEED);
        return Response.ok(articles).build();
    }

    @Timed
    @Path("/tagStubs")
    @GET
    public Response tagStubs(@Auth User user) {
        User u = userDao.findByEmail(user.getEmail());
        Set<String> tags = userDao.getSources(u.getId()).stream().map(Source::getTags).filter(Objects::nonNull).flatMap(List::stream).collect(Collectors.toSet());
        return Response.ok(tags).build();
    }

    @Timed
    @Path("/bookmark")
    @Consumes("application/json")
    @POST
    public Response bookmark(@Auth User user, Article article) {
        User u = userDao.findByEmail(user.getEmail());
        articleDao.bookmarkArticle(u.getId(), article);
        return Response.ok().build();
    }

    @Timed
    @Path("/bookmarked")
    @GET
    public Response bookmarked(@Auth User user) {
        User u = userDao.findByEmail(user.getEmail());
        List<Article> bookmarked = articleDao.getBookmarked(u.getId());
        return Response.ok(bookmarked).build();
    }

    @Timed
    @Path("/new")
    @POST
    public Response addFeed(@Auth User user, List<String> feeds) {
        if (feeds.isEmpty()) {
            throw new BadRequestException();
        }

        User u = userDao.findByEmail(user.getEmail());

        Set<String> pruned = new HashSet<>(feeds);
        List<String> existing = userDao.getSources(u.getId()).stream().map(s -> s.getUrl().toString()).collect(Collectors.toList());

        List<FeedParser> feedResponses = new ArrayList<>();
        pruned.forEach((String f) -> {
            if (existing.contains(f)) {
                feedResponses.add(FeedParser.invalid("Already Subscribed to Feed!", f));
            } else {
                FeedParser parser = FeedParser.parse(f);
                if (parser.isValid()) {
                    userDao.addFeed(u.getId(), parser.getUrl());
                }
                feedResponses.add(parser);
            }
        });
        return Response.ok(feedResponses).build();
    }
}
