package com.example.authapi.mapper;

import com.example.authapi.model.RefreshToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RefreshTokenMapper {
  RefreshToken selectByToken(@Param("token") String token);

  void revokeAllByUserId(@Param("userId") Long userId);

  int countByTokenAndRevokedFalse(@Param("token") String token);

  void insert(RefreshToken token);

  void delete(RefreshToken token);
}
