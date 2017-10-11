package com.tanza.rufus.core;

import java.io.Serializable;

/**
 * Created by jtanza.
 */
public class Credentials implements Serializable {
    private String email;
    private String password;

    public Credentials() {}

    public Credentials(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
