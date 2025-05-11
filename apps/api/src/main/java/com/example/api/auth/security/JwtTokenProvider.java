package com.example.api.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

  @Value("${jwt.secret:defaultSecretKey12345678901234567890123456789012}")
  private String jwtSecret;

  @Value("${jwt.access-token-expiration:300000}") // 5 minutes
  private long accessTokenExpiration;

  @Value("${jwt.refresh-token-expiration:604800000}") // 7 days
  private long refreshTokenExpiration;

  private final UserDetailsService userDetailsService;
  private Key key;

  public JwtTokenProvider(UserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  @PostConstruct
  protected void init() {
    this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
  }

  public String generateAccessToken(String username) {
    return generateToken(username, accessTokenExpiration);
  }

  public String generateRefreshToken(String username) {
    return generateToken(username, refreshTokenExpiration);
  }

  private String generateToken(String username, long expiration) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expiration);

    return Jwts.builder()
        .setSubject(username)
        .setIssuer("auth-api")
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(key, SignatureAlgorithm.HS512)
        .compact();
  }

  public String getUsernameFromToken(String token) {
    Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

    return claims.getSubject();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (SecurityException ex) {
      log.warn("Invalid JWT signature: {}", ex.getMessage());
    } catch (MalformedJwtException ex) {
      log.warn("Invalid JWT token: {}", ex.getMessage());
    } catch (ExpiredJwtException ex) {
      log.warn("Expired JWT token: {}", ex.getMessage());
    } catch (UnsupportedJwtException ex) {
      log.warn("Unsupported JWT token: {}", ex.getMessage());
    } catch (IllegalArgumentException ex) {
      log.warn("JWT claims string is empty: {}", ex.getMessage());
    }
    return false;
  }

  public Authentication getAuthentication(String token) {
    String username = getUsernameFromToken(token);
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }
}
