package com.tanza.rufus.resources;

import com.tanza.rufus.auth.AuthUtils;
import com.tanza.rufus.auth.BasicAuthenticator;
import com.tanza.rufus.auth.TokenGenerator;
import com.tanza.rufus.core.NewUser;
import com.tanza.rufus.core.User;
import com.tanza.rufus.core.UserUtils;
import com.tanza.rufus.db.ArticleDao;
import com.tanza.rufus.db.UserDao;
import com.tanza.rufus.feed.FeedConstants;

import org.apache.commons.collections.CollectionUtils;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;
import java.util.Optional;

@Path("/user")
public class UserResource {

    private final BasicAuthenticator authenticator;
    private final TokenGenerator tokenGenerator;
    private final UserDao userDao;
    private final ArticleDao articleDao;

    public UserResource(
        BasicAuthenticator authenticator,
        TokenGenerator tokenGenerator,
        UserDao userDao,
        ArticleDao articleDao
    ) {
        this.authenticator = authenticator;
        this.tokenGenerator = tokenGenerator;
        this.userDao = userDao;
        this.articleDao = articleDao;
    }

    @Path("/login")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @POST
    public Response login(@FormDataParam("email") String email, @FormDataParam("password") String password) {
        if (!UserUtils.valid(email, password)) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        Optional<User> optional = authenticator.authenticate(email, password);
        if (optional.isPresent()) {
            //generate and return jwt token to client
            return Response.ok(tokenGenerator.generateToken(email)).build();
        } else {
            return Response.status(Status.UNAUTHORIZED).build();
        }
    }

    @Path("/new")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public Response newUser(NewUser newUser) {
        String email = newUser.getEmail();
        String pw = newUser.getPassword();

        if (!UserUtils.valid(email, pw)) {
            return Response.status(Status.BAD_REQUEST)
                .type(MediaType.TEXT_PLAIN)
                .entity("username or email empty")
                .build();
        }

        if (userDao.findByEmail(email) != null) {
            return Response.status(Status.BAD_REQUEST)
                .type(MediaType.TEXT_PLAIN)
                .entity("a user with that email address already exists")
                .build();
        }

        User user = new User(email, AuthUtils.hashPassword(pw));
        user = userDao.addUser(user);

        List<String> starterFeeds = newUser.getStarterFeeds();
        if (CollectionUtils.isNotEmpty(starterFeeds) && UserUtils.validStarterFeeds(starterFeeds)) {
            for (String feed : starterFeeds) {
                articleDao.addSource(
                    user.getId(),
                    FeedConstants.STARTER_FEEDS.get(feed)
                );
            }
        }

        return Response.ok(tokenGenerator.generateToken(email)).build();
    }
}
