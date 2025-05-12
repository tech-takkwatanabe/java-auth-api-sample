package com.example.api.auth.domain.entity;

import lombok.*;
import java.time.LocalDateTime;
import com.example.api.auth.domain.vo.UserId;
import com.example.api.auth.domain.vo.UUID;
import com.example.api.auth.domain.vo.Email;

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
