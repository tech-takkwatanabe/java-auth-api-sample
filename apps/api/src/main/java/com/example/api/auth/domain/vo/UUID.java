package com.example.api.auth.domain.vo;

import com.github.f4b6a3.uuid.UuidCreator;

public class UUID {

  private final java.util.UUID value;

  public UUID(java.util.UUID value) {
    this.value = value;
  }

  public static UUID random() {
    // UUIDv7（タイムソート可能）を生成
    return new UUID(UuidCreator.getTimeOrderedEpoch());
  }

  public static UUID from(java.util.UUID value) {
    return new UUID(value);
  }

  public static UUID from(String value) {
    return new UUID(java.util.UUID.fromString(value));
  }

  public java.util.UUID getValue() {
    return value;
  }

  @Override
  public String toString() {
    return value.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    UUID uuid = (UUID) o;
    return value.equals(uuid.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  public static UUID randomUUID() {
    return UUID.random();
  }
}
