package com.example.api.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;
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

  @Value("${jwt.access-token-expiration:86400000}")
  private long accessTokenExpiration;

  @Value("${jwt.refresh-token-expiration:604800000}")
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

  public String generateAccessToken(UUID userUuid) {
    return generateToken(userUuid, accessTokenExpiration);
  }

  public String generateRefreshToken(UUID userUuid) {
    return generateToken(userUuid, refreshTokenExpiration);
  }

  private String generateToken(UUID userUuid, long expiration) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expiration);

    return Jwts.builder()
        .setSubject(userUuid.toString())
        .claim("user_uuid", userUuid.toString())
        .setIssuer("auth-api")
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(key, SignatureAlgorithm.HS512)
        .compact();
  }

  public UUID getUserUuidFromToken(String token) {
    Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    return UUID.fromString(claims.get("user_uuid", String.class));
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (SecurityException
        | MalformedJwtException
        | ExpiredJwtException
        | UnsupportedJwtException
        | IllegalArgumentException ex) {
      log.warn("JWT validation failed: {}", ex.getMessage());
      return false;
    }
  }

  public Authentication getAuthentication(String token) {
    UUID userUuid = getUserUuidFromToken(token);
    UserDetails userDetails = userDetailsService.loadUserByUsername(userUuid.toString());
    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }

  public Duration getRefreshTokenDuration() {
    return Duration.ofMillis(refreshTokenExpiration);
  }
}
