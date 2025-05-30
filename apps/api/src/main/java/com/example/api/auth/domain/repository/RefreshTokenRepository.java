package com.example.api.auth.domain.repository;

import com.example.api.auth.domain.entity.RefreshToken;
import com.example.api.auth.domain.vo.UUID;
import java.util.Optional;

public interface RefreshTokenRepository {
  void save(RefreshToken token);

  Optional<RefreshToken> findByUuid(UUID uuid);

  void deleteByUuid(UUID uuid);
}
