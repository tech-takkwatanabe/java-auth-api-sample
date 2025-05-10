package com.example.authapi.service;

import com.example.authapi.dto.request.LoginRequest;
import com.example.authapi.dto.request.SignupRequest;
import com.example.authapi.dto.request.TokenRefreshRequest;
import com.example.authapi.dto.response.JwtResponse;
import com.example.authapi.dto.response.MessageResponse;
import com.example.authapi.dto.response.TokenRefreshResponse;
import com.example.authapi.dto.response.UserResponse;
import com.example.authapi.exception.TokenRefreshException;
import com.example.authapi.model.RefreshToken;
import com.example.authapi.model.User;
import com.example.authapi.repository.RefreshTokenRepository;
import com.example.authapi.repository.UserRepository;
import com.example.authapi.security.JwtTokenProvider;
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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public MessageResponse registerUser(SignupRequest signupRequest) {
        // Check if username is already taken
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return MessageResponse.builder()
                    .message("Error: Username is already taken!")
                    .build();
        }

        // Check if email is already in use
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return MessageResponse.builder()
                    .message("Error: Email is already in use!")
                    .build();
        }

        // Create new user
        User user = User.builder()
                .username(signupRequest.getUsername())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .build();

        userRepository.save(user);

        return MessageResponse.builder()
                .message("User registered successfully!")
                .build();
    }

    @Transactional
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String username = authentication.getName();
        String accessToken = jwtTokenProvider.generateAccessToken(username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Revoke any existing refresh tokens for this user
        refreshTokenRepository.revokeAllUserTokens(user.getId());
        
        // Create new refresh token
        String refreshTokenString = jwtTokenProvider.generateRefreshToken(username);
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenString)
                .expiryDate(Instant.now().plusMillis(604800000)) // 7 days
                .revoked(false)
                .build();
        
        refreshTokenRepository.save(refreshToken);

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

        return refreshTokenRepository.findByToken(requestRefreshToken)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername());
                    return TokenRefreshResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(requestRefreshToken)
                            .tokenType("Bearer")
                            .build();
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token not found!"));
    }

    private RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isRevoked() || token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired or revoked. Please login again.");
        }
        return token;
    }

    @Transactional
    public MessageResponse logoutUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        
        refreshTokenRepository.revokeAllUserTokens(user.getId());
        
        return MessageResponse.builder()
                .message("Logged out successfully!")
                .build();
    }

    public UserResponse getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }
}