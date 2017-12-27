package com.tanza.rufus.resources;

import com.tanza.rufus.api.Article;
import com.tanza.rufus.api.Source;
import com.tanza.rufus.api.Source.ClientSource;
import com.tanza.rufus.core.User;
import com.tanza.rufus.db.ArticleDao;
import com.tanza.rufus.db.UserDao;
import com.tanza.rufus.feed.FeedConstants;
import com.tanza.rufus.feed.FeedParser;
import com.tanza.rufus.feed.FeedParser.FeedResponse;
import com.tanza.rufus.feed.FeedProcessor;
import com.tanza.rufus.feed.FeedUtils;
import com.tanza.rufus.views.ArticleView;

import io.dropwizard.auth.Auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Resource for {@link Article} related processing.
 *
 * All methods with an {@link Optional<User>} method parameter denote endpoints
 * supporting anonymous user sessions; data from these endpoints are generated
 * from the collection of {@link FeedConstants#STARTER_FEEDS}.
 *
 * @author jtanza
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
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @GET
    public Response frontPage(@Auth Optional<User> user) {
        return user.isPresent()
            ? articleView(() -> processor.buildFrontpageCollection(user.get()))
            : articleView(processor::buildFrontpageCollection);
    }

    @Path("/all")
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @GET
    public Response all(@Auth Optional<User> user) {
        return  user.isPresent()
            ? articleView(() -> processor.buildArticleCollection(user.get()))
            : articleView(processor::buildArticleCollection);
    }

    @Path("/tagged")
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @GET
    public Response byTag(@Auth Optional<User> user, @QueryParam("tag") String tag) {
        return user.isPresent()
            ? articleView(() -> processor.buildTagCollection(user.get(), tag))
            : articleView(() -> processor.buildTagCollection(tag));
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
            return ResourceUtils.badRequest("Article is already bookmarked.");
        }
        articleDao.bookmarkArticle(user.getId(), article);
        return Response.ok().build();
    }

    @Path("/removeBookmark")
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    public Response removeBookmark(@Auth User user, Article article) {
        user = userDao.findByEmail(user.getEmail());
        articleDao.removeArticle(user.getId(), article.getUrl());
        return Response.ok().build();
    }

    @Path("/bookmarked")
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @GET
    public Response bookmarked(@Auth User user) {
        final User u = userDao.findByEmail(user.getEmail());
        return articleView(() -> articleDao.getBookmarked(u.getId()));
    }

    @Path("/new")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public Response addFeed(@Auth User user, List<String> feeds) {
        if (feeds.isEmpty()) {
            return ResourceUtils.badRequest("No feeds provided.");
        }
        return Response.ok(FeedResponse.formatMessage(parser.parse(user, feeds))).build();
    }

    @Path("/setFrontpage")
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    public Response setFrontpage(@Auth User user, String url) {
        user = userDao.findByEmail(user.getEmail());
        try {
            Source source = new Source(new URL(url));
            articleDao.setFrontpage(user.getId(), source);
            processor.invalidateCache(user.getId());
            return Response.ok().build();
        } catch (MalformedURLException e) {
            return ResourceUtils.badRequest(e.getMessage());
        }
    }

    @Path("/removeFrontpage")
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    public Response removeFrontpage(@Auth User user, String url) {
        user = userDao.findByEmail(user.getEmail());
        try {
            Source source = new Source(new URL(url));
            articleDao.removeFrontpage(user.getId(), source);
            processor.invalidateCache(user.getId());
            return Response.ok().build();
        } catch (MalformedURLException e) {
            return ResourceUtils.badRequest(e.getMessage());
        }
    }

    @Path("sources")
    @GET
    public Response getSources(@Auth User user) {
        user = userDao.findByEmail(user.getEmail());
        return Response.ok(articleDao.getSources(user.getId())).build();
    }

    @Path("userFeeds")
    @GET
    public Response userFeeds(@Auth User user) {
        user = userDao.findByEmail(user.getEmail());
        List<ClientSource> sources = articleDao.getSources(user.getId())
            .stream()
            .map(ClientSource::ofExisting)
            .collect(Collectors.toList());
        return Response.ok(sources).build();
    }

    @Path("unsubscribe")
    @PUT
    public Response unsubscribe(@Auth User user, String url) {
        user = userDao.findByEmail(user.getEmail());
        try {
            URL parseUrl = new URL(url); //ensure arg is a valid url
            parseUrl.toURI();
            articleDao.removeSource(url);
            processor.invalidateCache(user.getId());
            return Response.ok().build();
        } catch (MalformedURLException | URISyntaxException e) {
            return ResourceUtils.badRequest(e.getMessage());
        }
    }

    private Response articleView(Supplier<Collection<Article>> articleSupplier) {
        Collection<Article> articles = articleSupplier.get();
        if (articles.isEmpty() || FeedUtils.isNull(articles)) {
            return Response.ok(ArticleView.EMPTY).build();
        }
        return Response.ok(ArticleView.ofArticles(articles)).build();
    }
}
