package com.tanza.rufus.resources;

import com.tanza.rufus.auth.BasicAuthenticator;
import com.tanza.rufus.core.Login;
import com.tanza.rufus.core.User;
import com.tanza.rufus.db.UserDao;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Optional;


/**
 * Created by jtanza.
 */
@Path("/user")
public class UserResource {

    private final UserDao userDao;

    public UserResource(UserDao userDao) {
        this.userDao = userDao;
    }

    @Path("/login")
    @POST
    public Response login(@FormParam("login") Login login) {
        BasicAuthenticator authenticator = new BasicAuthenticator(userDao);
        Optional<User> user = authenticator.authenticate(login);
        return user.isPresent() ? Response.ok(user).build() : Response.status(Status.UNAUTHORIZED).build();
    }
}
