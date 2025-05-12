package com.example.api.auth.domain.repository;

import com.example.api.auth.model.RefreshToken;
import java.util.Optional;

public interface RefreshTokenRepository {
  Optional<RefreshToken> findByToken(String token);

  void revokeAllUserTokens(Long userId);

  boolean existsByTokenAndRevokedFalse(String token);
}
