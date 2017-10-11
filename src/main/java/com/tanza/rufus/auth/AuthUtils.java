package com.tanza.rufus.auth;

import com.tanza.rufus.core.User;
import org.mindrot.jbcrypt.BCrypt;

/**
 * @author jtanza
 */
public class AuthUtils {

    private AuthUtils() {
        throw new AssertionError();
    }

    /**
     * Hash an {@link User}'s plaintext password for storage
     * in the db.
     *
     * Utilizes jBCrypt
     *
     * @param password
     * @return
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Verifies a stored hashed password against a plaintext equivalent.
     *
     * Utilizes jBCrypt
     *
     * @param password
     * @param hash
     * @return
     */
    public static boolean isPassword(String password, String hash) {
        return hash != null && BCrypt.checkpw(password, hash);
    }
}
