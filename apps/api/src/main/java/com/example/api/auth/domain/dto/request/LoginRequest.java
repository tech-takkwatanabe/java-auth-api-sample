package com.example.api.auth.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
  @NotBlank(message = "email is required")
  private String email;

  @NotBlank(message = "Password is required")
  private String password;
}
