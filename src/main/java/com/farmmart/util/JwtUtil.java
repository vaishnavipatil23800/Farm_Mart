package com.farmmart.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

// replaces: server/utils/generatedAccessToken.js
// AND: jwt.verify() calls in auth.js

@Component  // Spring will create one instance and reuse it everywhere
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;  // reads from application.properties

    @Value("${jwt.expiration-ms}")
    private long expirationMs;  // 18000000 = 5 hours in milliseconds

    // Creates the signing key from our secret string
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // replaces: jwt.sign({ id: userId }, process.env.SECRET_KEY_ACCESS_TOKEN, { expiresIn: '5h' })
    public String generateToken(String userId) {
        return Jwts.builder()
                .subject(userId)             // stores userId in the token
                .issuedAt(new Date())        // when token was created
                .expiration(new Date(System.currentTimeMillis() + expirationMs))  // expiry
                .signWith(getKey())          // sign with our secret
                .compact();                  // build the token string
    }

    // replaces: jwt.verify(token, process.env.SECRET_KEY_ACCESS_TOKEN)
    // Returns the userId that was stored in the token
    public String extractUserId(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Returns true if token is valid, false if expired or tampered
    public boolean isTokenValid(String token) {
        try {
            extractUserId(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
