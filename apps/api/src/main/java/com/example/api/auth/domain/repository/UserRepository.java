package com.example.api.auth.domain.repository;

import com.example.api.auth.model.User;
import java.util.Optional;

public interface UserRepository {
  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

  Optional<User> findByUuid(String uuid);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);
}
