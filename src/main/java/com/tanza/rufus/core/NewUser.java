package com.tanza.rufus.core;

import org.apache.commons.validator.routines.EmailValidator;

import java.io.Serializable;

/**
 * @author jtanza
 */
public class NewUser implements Serializable {
    private static final long serialVersionUID = 1L;

    private String email;
    private String password;

    public NewUser() {}

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public static boolean validFields(String email, String password) {
        return EmailValidator.getInstance().isValid(email) && password != null;
    }
}
