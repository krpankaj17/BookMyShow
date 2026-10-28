package com.bookMyShow.user_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long accessExpirationMs,
            @Value("${jwt.refresh-expiration-ms}") long refreshExpirationMs) {

        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessExpirationMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }


    public String generateAccessToken(
            String userId,
            String email,
            List<String> authorities) {

        Date now = new Date();

        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("type", "ACCESS")
                .claim("authorities", authorities)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessExpirationMs))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(String userId, String email) {
        Date now = new Date();

        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("type", "REFRESH")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshExpirationMs))
                .signWith(secretKey)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractUserId(String token) {
        return extractClaims(token).get("userId", String.class);
    }

    public List<String> extractAuthorities(String token) {
        return extractAuthorities(extractClaims(token));
    }

    public List<String> extractAuthorities(Claims claims) {
        List<?> authorities =
                claims.get("authorities", List.class);

        if (authorities == null) {
            return List.of();
        }

        return authorities.stream()
                .map(Object::toString)
                .toList();
    }

    public boolean isAccessToken(String token) {
        try {
            return "ACCESS".equals(
                    extractClaims(token).get("type", String.class)
            );
        } catch (io.jsonwebtoken.JwtException |
                 IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            return "REFRESH".equals(
                    extractClaims(token).get("type", String.class)
            );
        } catch (io.jsonwebtoken.JwtException |
                 IllegalArgumentException e) {
            return false;
        }
    }

    public long getAccessTokenExpirationSeconds() {
        return accessExpirationMs / 1000;
    }
}