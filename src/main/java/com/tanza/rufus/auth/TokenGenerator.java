package com.tanza.rufus.auth;

import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;

import static org.jose4j.jws.AlgorithmIdentifiers.HMAC_SHA256;

/**
 * Service responsible for generating valid JWT Tokens
 *
 * @author jtanza
 */
public class TokenGenerator {
    private static final float TOKEN_EXPIRATION_IN_MINUTES = 60;

    private final byte[] tokenSecret;

    public TokenGenerator(byte[] tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    public String generateToken(String subject) {
        final JwtClaims claims = new JwtClaims();
        claims.setSubject(subject);
        claims.setExpirationTimeMinutesInTheFuture(TOKEN_EXPIRATION_IN_MINUTES);

        final JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setAlgorithmHeaderValue(HMAC_SHA256);
        jws.setKey(new HmacKey(tokenSecret));
        jws.setDoKeyValidation(false); //relaxes hmac key length restrictions

        try {
            return jws.getCompactSerialization();
        } catch (JoseException e) {
            throw new RuntimeException(e);
        }
    }
}
