package com.tanza.rufus.auth;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author jtanza
 */
public class AuthUtilsTest {

    @Test
    public void testPwHash() {
        String pw = "new_super_secret_pw";
        String hashed = AuthUtils.hashPassword(pw);

        Assert.assertFalse(pw.equals(hashed));
        Assert.assertTrue(AuthUtils.isPassword(pw, hashed));
    }
}
