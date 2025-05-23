package com.example.api.auth.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenRefreshRequest {
  @NotBlank(message = "Refresh token is required")
  private String refreshToken;
}
