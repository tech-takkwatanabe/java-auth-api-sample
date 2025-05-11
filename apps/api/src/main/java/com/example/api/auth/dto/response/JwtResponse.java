package com.example.api.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
  private String accessToken;
  private String refreshToken;
  private String tokenType;
  private Long userId;
  private String username;
  private String email;
}
