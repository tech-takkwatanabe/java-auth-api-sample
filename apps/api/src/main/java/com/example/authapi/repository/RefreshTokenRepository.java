package com.example.authapi.repository;

import com.example.authapi.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {
  Optional<RefreshToken> findByToken(String token);

  void revokeAllUserTokens(Long userId);

  boolean existsByTokenAndRevokedFalse(String token);
}
