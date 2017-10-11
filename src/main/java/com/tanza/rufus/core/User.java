package com.tanza.rufus.core;

import java.security.Principal;
import java.util.List;

/**
 * Created by jtanza.
 */
public class User implements Principal {
    private long id;
    private String email;
    private String password;

    public User() {} //dummy jackson constructor

    @Override
    public String getName() {
        return email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
