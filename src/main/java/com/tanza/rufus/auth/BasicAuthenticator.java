package com.tanza.rufus.auth;

import com.tanza.rufus.core.Credentials;
import com.tanza.rufus.core.User;
import com.tanza.rufus.db.UserDao;
import com.tanza.rufus.resources.UserResource;

import java.util.Optional;


/**
 * Provides basic authentication against
 * access to {@link UserResource#login(Credentials)}.
 *
 * Created by jtanza.
 */
public class BasicAuthenticator {
    private final UserDao userDao;

    public BasicAuthenticator(UserDao userDao) {
        this.userDao = userDao;
    }

    public Optional<User> authenticate(String email, String password) {
        User u = userDao.findByEmail(email);
        return u != null && AuthUtils.isPassword(password, u.getPassword()) ? Optional.of(u) : Optional.empty();
    }
}
