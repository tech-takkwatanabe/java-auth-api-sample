package com.example.api.auth.domain.entity;

import com.example.api.auth.domain.vo.Email;
import com.example.api.auth.domain.vo.UUID;
import com.example.api.auth.domain.vo.UserId;
import java.time.LocalDateTime;
import lombok.*;

@Data
public class User {
  private UserId id;
  private UUID uuid;
  private String username;
  private Email email;
  private String password;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
