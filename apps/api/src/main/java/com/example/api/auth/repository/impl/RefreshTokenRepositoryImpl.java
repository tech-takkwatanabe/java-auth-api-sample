package com.example.api.auth.repository.impl;

import com.example.api.auth.mapper.RefreshTokenMapper;
import com.example.api.auth.model.RefreshToken;
import com.example.api.auth.repository.RefreshTokenRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

  private final RefreshTokenMapper refreshTokenMapper;

  public RefreshTokenRepositoryImpl(RefreshTokenMapper refreshTokenMapper) {
    this.refreshTokenMapper = refreshTokenMapper;
  }

  @Override
  public Optional<RefreshToken> findByToken(String token) {
    return Optional.ofNullable(refreshTokenMapper.selectByToken(token));
  }

  @Override
  public void revokeAllUserTokens(Long userId) {
    refreshTokenMapper.revokeAllByUserId(userId);
  }

  @Override
  public boolean existsByTokenAndRevokedFalse(String token) {
    return refreshTokenMapper.countByTokenAndRevokedFalse(token) > 0;
  }
}
