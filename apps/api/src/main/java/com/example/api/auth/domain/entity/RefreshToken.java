package com.example.api.auth.domain.entity;

import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
  private String userUuid;
  private String token;
  private Instant expiryDate;
  private boolean revoked;
}
