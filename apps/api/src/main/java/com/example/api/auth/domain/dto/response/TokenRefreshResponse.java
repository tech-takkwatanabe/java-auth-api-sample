package com.example.api.auth.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenRefreshResponse {
  private String accessToken;
  private String refreshToken;
  private String tokenType;
}
