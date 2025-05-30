package com.example.api.auth.domain.vo;

import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode
public class Email {
  String value;

  private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

  public Email(String value) {
    if (value == null || !EMAIL_REGEX.matcher(value).matches()) {
      throw new IllegalArgumentException("Invalid email format: " + value);
    }
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
