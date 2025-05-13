package com.example.api.auth.domain.entity;

import com.example.api.auth.domain.vo.UUID;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
  private UUID userUuid;
  private String token;
  private Instant expiryDate;
  private boolean revoked;
}
