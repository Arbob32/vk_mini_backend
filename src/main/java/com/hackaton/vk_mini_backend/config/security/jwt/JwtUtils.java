package com.hackaton.vk_mini_backend.config.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtUtils {
    @Value("${app.auth.jwtSecret}")
    private String jwtSecret;

    @Value("${app.auth.jwtExpirationMs}")
    private long jwtExpirationMs;

    private SecretKey key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        log.info("JWT Secret configured, key algorithm: {}", key.getAlgorithm());
    }

    public String generateJwtToken(final String login) {
        return Jwts.builder()
                .setSubject(login)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(key)
                .compact();
    }

    public String generateJwtToken(final String login, final Claims claims) {
        return Jwts.builder()
                .setSubject(login)
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(key)
                .compact();
    }

    public String getUserNameFromJwtToken(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(final String authToken) {
        boolean isValidate = false;
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            isValidate = true;
        } catch (ExpiredJwtException ex) {
            log.error("JWT токен просрочен: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("JWT токен не поддерживается: {}", ex.getMessage());
        } catch (SignatureException ex) {
            log.error("Недействительная JWT подпись: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Недействительный JWT токен: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("Строка JWT токена пуста: {}", ex.getMessage());
        }

        return isValidate;
    }
}
