package com.example.api.auth.domain.repository.impl;

import com.example.api.auth.domain.entity.RefreshToken;
import com.example.api.auth.domain.repository.RefreshTokenRepository;
import com.example.api.auth.domain.vo.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RedisRefreshTokenRepositoryImpl implements RefreshTokenRepository {

  private final RedisTemplate<String, RefreshToken> redisTemplate;
  private static final String PREFIX = "refresh_token:";

  @Override
  public void save(RefreshToken token) {
    Duration ttl = Duration.between(Instant.now(), token.getExpiryDate());
    redisTemplate.opsForValue().set(key(token.getUserUuid()), token, ttl);
  }

  @Override
  public Optional<RefreshToken> findByUuid(UUID uuid) {
    return Optional.ofNullable(redisTemplate.opsForValue().get(key(uuid)));
  }

  @Override
  public void deleteByUuid(UUID uuid) {
    redisTemplate.delete(key(uuid));
  }

  private String key(UUID uuid) {
    return PREFIX + uuid.getValue();
  }
}
