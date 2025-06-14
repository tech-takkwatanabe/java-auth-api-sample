package com.example.api.auth.controller;

import com.example.api.auth.domain.dto.request.LoginRequest;
import com.example.api.auth.domain.dto.request.SignupRequest;
import com.example.api.auth.domain.dto.request.TokenRefreshRequest;
import com.example.api.auth.domain.dto.response.JwtResponse;
import com.example.api.auth.domain.dto.response.MessageResponse;
import com.example.api.auth.domain.dto.response.TokenRefreshResponse;
import com.example.api.auth.domain.dto.response.UserResponse;
import com.example.api.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management API")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/signup")
  @Operation(summary = "Register a new user", description = "Create a new user account")
  public ResponseEntity<MessageResponse> registerUser(
      @Valid @RequestBody SignupRequest signupRequest) {
    MessageResponse response = authService.registerUser(signupRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/login")
  @Operation(summary = "User login", description = "Authenticate user and return JWT tokens")
  public ResponseEntity<JwtResponse> authenticateUser(
      @Valid @RequestBody LoginRequest loginRequest) {
    JwtResponse response = authService.authenticateUser(loginRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/refresh")
  @Operation(summary = "Refresh token", description = "Get new access token using refresh token")
  public ResponseEntity<TokenRefreshResponse> refreshToken(
      @Valid @RequestBody TokenRefreshRequest request) {
    TokenRefreshResponse response = authService.refreshToken(request);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/logout")
  @Operation(
      summary = "User logout",
      description = "Revoke refresh tokens and log out the user",
      security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<MessageResponse> logoutUser(HttpServletRequest request) {
    MessageResponse response = authService.logoutUser(request);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/me")
  @Operation(
      summary = "Current user",
      description = "Get the current authenticated user's information",
      security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<UserResponse> getCurrentUser(HttpServletRequest request) {
    UserResponse response = authService.getCurrentUser(request);
    return ResponseEntity.ok(response);
  }
}
