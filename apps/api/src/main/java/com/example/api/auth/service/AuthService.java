package com.example.api.auth.service;

import com.example.api.auth.domain.dto.request.LoginRequest;
import com.example.api.auth.domain.dto.request.SignupRequest;
import com.example.api.auth.domain.dto.request.TokenRefreshRequest;
import com.example.api.auth.domain.dto.response.JwtResponse;
import com.example.api.auth.domain.dto.response.MessageResponse;
import com.example.api.auth.domain.dto.response.TokenRefreshResponse;
import com.example.api.auth.domain.dto.response.UserResponse;
import com.example.api.auth.domain.repository.RefreshTokenRepository;
import com.example.api.auth.domain.vo.UUID;
import com.example.api.auth.exception.TokenRefreshException;
import com.example.api.auth.mapper.UserMapper;
import com.example.api.auth.model.User;
import com.example.api.auth.security.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import com.example.api.auth.util.JwtUtils;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final AuthenticationManager authenticationManager;
  private final RefreshTokenRepository refreshTokenRepository;

  @Transactional
  public MessageResponse registerUser(SignupRequest signupRequest) {
    if (userMapper.countByUsername(signupRequest.getUsername()) > 0) {
      return MessageResponse.builder().message("Error: Username is already taken!").build();
    }

    if (userMapper.countByEmail(signupRequest.getEmail()) > 0) {
      return MessageResponse.builder().message("Error: Email is already in use!").build();
    }

    User user = User.builder()
        .uuid(UUID.randomUUID())
        .username(signupRequest.getUsername())
        .email(signupRequest.getEmail())
        .password(passwordEncoder.encode(signupRequest.getPassword()))
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    userMapper.insert(user);
    return MessageResponse.builder().message("User registered successfully!").build();
  }

  @Transactional
  public JwtResponse authenticateUser(LoginRequest loginRequest) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.getEmail(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    String email = authentication.getName();
    User user = Optional.ofNullable(userMapper.selectByEmail(email))
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

    refreshTokenRepository.deleteByUuid(user.getUuid());

    String accessToken = jwtTokenProvider.generateAccessToken(user.getUuid().getValue());
    String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUuid().getValue());

    com.example.api.auth.domain.entity.RefreshToken entityRefreshToken = com.example.api.auth.domain.entity.RefreshToken
        .builder()
        .userUuid(user.getUuid())
        .token(refreshToken)
        .expiryDate(Instant.now().plus(jwtTokenProvider.getRefreshTokenDuration()))
        .revoked(false)
        .build();

    refreshTokenRepository.save(entityRefreshToken);

    return JwtResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .tokenType("Bearer")
        .userId(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .build();
  }

  @Transactional
  public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
    String requestRefreshToken = request.getRefreshToken();

    UUID userUuid = new UUID(jwtTokenProvider.getUserUuidFromToken(requestRefreshToken));
    com.example.api.auth.domain.entity.RefreshToken token = refreshTokenRepository.findByUuid(userUuid)
        .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token not found!"));

    if (!token.getToken().equals(requestRefreshToken)) {
      throw new TokenRefreshException(requestRefreshToken, "Refresh token is invalid!");
    }

    token = verifyExpiration(token);
    String accessToken = jwtTokenProvider.generateAccessToken(userUuid.getValue());

    return TokenRefreshResponse.builder()
        .accessToken(accessToken)
        .refreshToken(requestRefreshToken)
        .tokenType("Bearer")
        .build();
  }

  private com.example.api.auth.domain.entity.RefreshToken verifyExpiration(
      com.example.api.auth.domain.entity.RefreshToken token) {
    if (token.isRevoked() || token.getExpiryDate().isBefore(Instant.now())) {
      refreshTokenRepository.deleteByUuid(token.getUserUuid());
      throw new TokenRefreshException(token.getToken(), "Refresh token was expired or revoked. Please login again.");
    }
    return token;
  }

  @Transactional
  public MessageResponse logoutUser() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = Optional.ofNullable(userMapper.selectByUsername(username))
        .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

    refreshTokenRepository.deleteByUuid(user.getUuid());

    return MessageResponse.builder().message("Logged out successfully!").build();
  }

  public UserResponse getCurrentUser(HttpServletRequest request) {
    String token = JwtUtils.getJwtFromRequest(request);
    // System.out.println("Extracted JWT token: " + token);
    if (token == null || !jwtTokenProvider.validateToken(token)) {
      throw new RuntimeException("Invalid or missing token");
    }

    UUID userUuid = new UUID(jwtTokenProvider.getUserUuidFromToken(token));
    User user = Optional.ofNullable(userMapper.selectByUuid(userUuid.toString()))
        .orElseThrow(() -> new UsernameNotFoundException("User not found with uuid: " + userUuid));

    return UserResponse.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .createdAt(user.getCreatedAt())
        .build();
  }
}
