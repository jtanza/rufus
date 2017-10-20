package com.tanza.rufus.resources;

import com.tanza.rufus.auth.AuthUtils;
import com.tanza.rufus.auth.BasicAuthenticator;
import com.tanza.rufus.auth.TokenGenerator;
import com.tanza.rufus.core.Credentials;
import com.tanza.rufus.core.NewUser;
import com.tanza.rufus.core.User;
import com.tanza.rufus.db.UserDao;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Optional;


/**
 * Created by jtanza.
 */
@Path("/user")
public class UserResource {

    private final BasicAuthenticator authenticator;
    private final TokenGenerator tokenGenerator;
    private final UserDao userDao;

    public UserResource(BasicAuthenticator authenticator, TokenGenerator tokenGenerator, UserDao userDao) {
        this.authenticator = authenticator;
        this.tokenGenerator = tokenGenerator;
        this.userDao = userDao;
    }

    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public Response login(Credentials credentials) {
        Optional<User> optional = authenticator.authenticate(credentials.getEmail(), credentials.getPassword());
        if (optional.isPresent()) {
            //generate and return jwt token to client
            return Response.ok(tokenGenerator.generateToken(credentials.getEmail())).build();
        } else {
            return Response.status(Status.UNAUTHORIZED).build();
        }
    }

    @Path("/new")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public Response newUser(NewUser newUser) {
        if (!NewUser.validFields(newUser.getEmail(), newUser.getPassword())) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        userDao.addUser(new User(newUser.getEmail(), AuthUtils.hashPassword(newUser.getPassword())));
        return Response.ok().build();
    }
}
