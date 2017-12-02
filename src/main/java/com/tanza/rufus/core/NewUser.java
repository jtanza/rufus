package com.tanza.rufus.core;

import java.io.Serializable;
import java.util.List;

public class NewUser implements Serializable {
    private static final long serialVersionUID = 1L;

    private String email;
    private String password;
    private List<String> starterFeeds;

    public NewUser() {}

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

    public List<String> getStarterFeeds() {
        return starterFeeds;
    }

    public void setStarterFeeds(List<String> starterFeeds) {
        this.starterFeeds = starterFeeds;
    }
}
