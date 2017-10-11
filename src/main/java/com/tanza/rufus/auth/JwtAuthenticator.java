package com.tanza.rufus.auth;

import com.tanza.rufus.core.User;
import com.tanza.rufus.db.UserDao;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.JwtContext;

import java.util.Optional;

/**
 * @author jtanza
 */
public class JwtAuthenticator implements Authenticator<JwtContext, User> {

    private final UserDao userDao;

    public JwtAuthenticator(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Optional<User> authenticate(JwtContext jwtContext) throws AuthenticationException {
        try {
            User u = userDao.findByEmail(jwtContext.getJwtClaims().getSubject());
            return u != null ? Optional.of(u) : Optional.empty();
        } catch (MalformedClaimException e) {
            return Optional.empty();
        }
    }
}
