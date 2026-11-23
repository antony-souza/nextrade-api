package com.antony.nextrade.config.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TokenProviderTest {
    private static final String KEY = "HWzT05Y1nVyN1xOu0qyXAsKp++nX6vXSiKQjvlYMJYU=";
    private final TokenProvider provider = new TokenProvider(KEY, 60_000);

    @Test
    void acceptsItsOwnSignedToken() {
        var userId = UUID.randomUUID();
        assertThat(provider.getUserId(provider.generateToken(userId))).contains(userId);
    }

    @Test
    void rejectsAnExpiredToken() {
        var token = Jwts.builder()
                .issuer("nextrade")
                .subject(UUID.randomUUID().toString())
                .expiration(Date.from(Instant.now().minusSeconds(60)))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(KEY)))
                .compact();

        assertThat(provider.getUserId(token)).isEmpty();
    }

    @Test
    void rejectsATokenSignedWithAnotherKey() {
        var token = Jwts.builder()
                .issuer("nextrade")
                .subject(UUID.randomUUID().toString())
                .expiration(Date.from(Instant.now().plusSeconds(60)))
                .signWith(Jwts.SIG.HS256.key().build())
                .compact();

        assertThat(provider.getUserId(token)).isEmpty();
    }
}
