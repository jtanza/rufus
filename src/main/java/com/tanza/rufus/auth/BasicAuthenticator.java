package com.tanza.rufus.auth;
import com.tanza.rufus.core.Login;
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

    public Optional<User> authenticate(Login login) {
        User u = userDao.findByEmail(login.getEmail());
        return u != null && u.getPassword().equals(login.getPassword()) ? Optional.of(u) : Optional.empty();
    }
}
