package com.tanza.rufus.core;

import com.tanza.rufus.api.Source;

import java.net.URL;
import java.security.Principal;
import java.util.List;

/**
 * Created by jtanza.
 */
public class User implements Principal {
    private int id;
    private String email;
    private String password;
    //TODO what to do with these? do we want to get users fully injected?
    private List<String> roles;

    @Override
    public String getName() {
        return email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
