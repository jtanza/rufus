package com.tanza.rufus.core;

/**
 * Created by jtanza.
 */
public class Login {
    private String email;
    private String password;

    public Login(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Login() {} //constructor for jackson de/serialization

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
