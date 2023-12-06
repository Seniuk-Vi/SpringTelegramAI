package org.brain.springtelegramai.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.brain.springtelegramai.config.JwtProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Component
@Slf4j
public record JwtUtils(JwtProperties jwtProperties) {
    public static final String ROLES = "roles";

    public String generateToken(UserDetails userDetails) {

        Date issuedDate = new Date(System.currentTimeMillis());
        Date expiredDate = new Date(System.currentTimeMillis() + jwtProperties.lifetime());
        var roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return provideToken(userDetails, roles, issuedDate, expiredDate);
    }
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .requireIssuer(jwtProperties().issuer())
                    .verifyWith(getSignInKey())
                    .build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
    private String provideToken(UserDetails userDetails, List<String> roles, Date issuedDate, Date expiredDate) {
        return Jwts.builder()
                .claim(ROLES, roles)
                .issuer(jwtProperties.issuer())
                .subject(userDetails.getUsername())
                .issuedAt(issuedDate)
                .expiration(expiredDate)
                .signWith(getSignInKey())
                .compact();
    }

    public String getUserEmail(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public List<SimpleGrantedAuthority> getRoles(String token) {

        List<String> roles = extractClaim(token, claims -> claims.get(ROLES, List.class));
        return roles.stream().map(SimpleGrantedAuthority::new).toList();
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaimsFromToken(token);
        var claimsRes = claimsResolver.apply(claims);
        System.out.println(claimsRes);
        return claimsRes;
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .requireIssuer(jwtProperties().issuer())
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Creates Key from BASE64 secret
     *
     * @return Key - SignInKey
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.secret());

        return Keys.hmacShaKeyFor(keyBytes);
    }
}
