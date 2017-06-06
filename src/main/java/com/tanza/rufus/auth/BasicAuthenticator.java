package com.tanza.rufus.auth;
import com.tanza.rufus.core.User;
import com.tanza.rufus.db.UserDao;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import java.util.Optional;


/**
 * Created by jtanza.
 */
public class BasicAuthenticator implements Authenticator<BasicCredentials, User> {
    private final UserDao userDao;

    public BasicAuthenticator(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Optional<User> authenticate(BasicCredentials creds) throws AuthenticationException {
        User u = userDao.findByEmail(creds.getUsername());
        return u != null && u.getPassword().equals(creds.getPassword()) ? Optional.of(u) : Optional.empty();
    }

    public Optional<User> authenticate(String email, String password) {
        User u = userDao.findByEmail(email);
        return u != null && u.getPassword().equals(password) ? Optional.of(u) : Optional.empty();
    }
}
