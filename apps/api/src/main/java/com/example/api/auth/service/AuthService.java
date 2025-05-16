package com.example.api.auth.service;

import com.example.api.auth.domain.dto.request.LoginRequest;
import com.example.api.auth.domain.dto.request.SignupRequest;
import com.example.api.auth.domain.dto.request.TokenRefreshRequest;
import com.example.api.auth.domain.dto.response.JwtResponse;
import com.example.api.auth.domain.dto.response.MessageResponse;
import com.example.api.auth.domain.dto.response.TokenRefreshResponse;
import com.example.api.auth.domain.dto.response.UserResponse;
import com.example.api.auth.exception.TokenRefreshException;
import com.example.api.auth.mapper.RefreshTokenMapper;
import com.example.api.auth.mapper.UserMapper;
import com.example.api.auth.model.RefreshToken;
import com.example.api.auth.model.User;
import com.example.api.auth.security.JwtTokenProvider;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.api.auth.domain.repository.RefreshTokenRepository;
import com.example.api.auth.domain.vo.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserMapper userMapper;
  private final RefreshTokenMapper refreshTokenMapper;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final AuthenticationManager authenticationManager;
  private final RefreshTokenRepository refreshTokenRepository;

  @Transactional
  public MessageResponse registerUser(SignupRequest signupRequest) {
    // Check if username is already taken
    if (userMapper.countByUsername(signupRequest.getUsername()) > 0) {
      return MessageResponse.builder().message("Error: Username is already taken!").build();
    }

    // Check if email is already in use
    if (userMapper.countByEmail(signupRequest.getEmail()) > 0) {
      return MessageResponse.builder().message("Error: Email is already in use!").build();
    }

    // Create new user
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

    String username = authentication.getName();
    String accessToken = jwtTokenProvider.generateAccessToken(username);

    User user = Optional.ofNullable(userMapper.selectByUsername(username))
        .orElseThrow(
            () -> new UsernameNotFoundException("User not found with username: " + username));

    // Revoke any existing refresh tokens for this user
    refreshTokenMapper.revokeAllByUserId(user.getId());

    // Create new refresh token
    String refreshTokenString = jwtTokenProvider.generateRefreshToken(username);
    com.example.api.auth.domain.entity.RefreshToken entityRefreshToken = com.example.api.auth.domain.entity.RefreshToken
        .builder()
        .userUuid(user.getUuid())
        .token(refreshTokenString)
        .expiryDate(Instant.now().plus(jwtTokenProvider.getRefreshTokenDuration()))
        .revoked(false)
        .build();

    refreshTokenRepository.save(entityRefreshToken);

    return JwtResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshTokenString)
        .tokenType("Bearer")
        .userId(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .build();
  }

  @Transactional
  public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
    String requestRefreshToken = request.getRefreshToken();

    RefreshToken token = refreshTokenMapper.selectByToken(requestRefreshToken);
    if (token == null) {
      throw new TokenRefreshException(requestRefreshToken, "Refresh token not found!");
    }

    token = verifyExpiration(token);
    User user = token.getUser();
    String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername());

    return TokenRefreshResponse.builder()
        .accessToken(accessToken)
        .refreshToken(requestRefreshToken)
        .tokenType("Bearer")
        .build();
  }

  private RefreshToken verifyExpiration(RefreshToken token) {
    if (token.isRevoked() || token.getExpiryDate().compareTo(Instant.now()) < 0) {
      refreshTokenMapper.delete(token);
      throw new TokenRefreshException(
          token.getToken(), "Refresh token was expired or revoked. Please login again.");
    }
    return token;
  }

  @Transactional
  public MessageResponse logoutUser() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = Optional.ofNullable(userMapper.selectByUsername(username))
        .orElseThrow(
            () -> new UsernameNotFoundException("User not found with username: " + username));

    refreshTokenMapper.revokeAllByUserId(user.getId());

    return MessageResponse.builder().message("Logged out successfully!").build();
  }

  public UserResponse getCurrentUser() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = Optional.ofNullable(userMapper.selectByUsername(username))
        .orElseThrow(
            () -> new UsernameNotFoundException("User not found with username: " + username));

    return UserResponse.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .createdAt(user.getCreatedAt())
        .build();
  }
}
