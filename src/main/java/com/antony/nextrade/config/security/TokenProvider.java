package com.antony.nextrade.config.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Component
public class TokenProvider {
    private final SecretKey signingKey;
    private final JwtParser parser;
    private final long expirationMillis;

    public TokenProvider(@Value("${jwt.key}") String key, @Value("${jwt.expiration}") long expirationMillis) {
        byte[] secret = Decoders.BASE64.decode(key);
        if (secret.length < 32 || expirationMillis < 1) {
            throw new IllegalArgumentException("Configure JWT_KEY com pelo menos 32 bytes em Base64 e JWT_EXPIRATION positivo");
        }

        this.signingKey = Keys.hmacShaKeyFor(secret);
        this.parser = Jwts.parser().verifyWith(signingKey).requireIssuer("nextrade").build();
        this.expirationMillis = expirationMillis;
    }

    public String generateToken(UUID userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer("nextrade")
                .subject(userId.toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMillis)))
                .signWith(signingKey)
                .compact();
    }

    public Optional<UUID> getUserId(String token) {
        try {
            var claims = parser.parseSignedClaims(token).getPayload();
            if (claims.getSubject() == null || claims.getExpiration() == null) {
                return Optional.empty();
            }
            return Optional.of(UUID.fromString(claims.getSubject()));
        } catch (JwtException | IllegalArgumentException exception) {
            return Optional.empty();
        }
    }
}
