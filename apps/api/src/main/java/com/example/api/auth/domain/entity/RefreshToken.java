package com.example.api.auth.domain.entity;

import lombok.Data;

import java.time.Instant;

@Data
public class RefreshToken {
  private Long id;
  private Long userId;
  private String token;
  private Instant expiryDate;
}
