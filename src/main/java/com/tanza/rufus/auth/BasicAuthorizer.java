package com.tanza.rufus.auth;

import com.tanza.rufus.core.User;
import io.dropwizard.auth.Authorizer;

/**
 * Created by jtanza.
 */
public class BasicAuthorizer implements Authorizer<User>{
    @Override
    public boolean authorize(User user, String role) {
        return user.getRoles() != null && user.getRoles().contains(role);
    }
}
