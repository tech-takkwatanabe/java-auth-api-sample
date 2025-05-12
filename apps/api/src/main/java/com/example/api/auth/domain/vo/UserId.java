package com.example.api.auth.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode
public class UserId {
  Long value;

  public UserId(Long value) {
    if (value == null || value <= 0) {
      throw new IllegalArgumentException("UserId must be positive.");
    }
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
