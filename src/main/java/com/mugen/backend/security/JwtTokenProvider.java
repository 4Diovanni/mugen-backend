package com.mugen.backend.security;

import com.mugen.backend.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret:mySecretKeyThatIsAtLeast32CharactersLongForHS512Algorithm}")
    private String jwtSecret;

    @Value("${jwt.expiration:3600000}")
    private long jwtExpirationMs;

    /**
     * Gera JWT ‘token’ com dados do usuário e a suas roles
     */
    public String generateToken(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(r -> "ROLE_" + r.getName())
                .collect(Collectors.toSet());

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("displayName", user.getDisplayName())
                .claim("roles", new ArrayList<>(roleNames))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key)
                .compact();
    }

    /**
     * Valida se o token é válido
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SignatureException e) {
            log.error("JWT signature validation failed: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        } catch (ExpiredJwtException e) {
            log.error("JWT token has expired: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is not supported: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extrai User ID do token
     */
    public String extractUserId(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Extrai roles do token
     */
    @SuppressWarnings("unchecked")
    public Set<String> extractRoles(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        List<String> roleList = (List<String>) claims.get("roles");
        return roleList != null ? new HashSet<>(roleList) : new HashSet<>();
    }
}
