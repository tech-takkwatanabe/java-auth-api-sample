package com.example.api.auth.domain.entity;

import java.time.Instant;
import lombok.*;

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
