package com.tanza.rufus.core;

import java.io.Serializable;
import java.security.Principal;

/**
 * Created by jtanza.
 */
public class User implements Principal, Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String email;
    private String password;

    public User() {} //jackson

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

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
