package com.example.api.auth.model;

import com.example.api.auth.domain.vo.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

  // user UUIDをRedisのキーとして使う想定
  private UUID userUuid;

  // トークン文字列（JWTなど）
  private String token;

  // 有効期限
  private Instant expiryDate;

  private User user;

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public boolean isRevoked() {
    return this.isRevoked();
  }

  public static class RefreshTokenBuilder {
    private boolean revoked;

    public RefreshTokenBuilder revoked(boolean revoked) {
      this.revoked = revoked;
      return this;
    }

    public boolean isRevoked() {
      return revoked;
    }

    public void setRevoked(boolean revoked) {
      this.revoked = revoked;
    }

    public RefreshToken build() {
      RefreshToken refreshToken = new RefreshToken();
      this.setRevoked(this.revoked);
      return refreshToken;
    }
  }
}
