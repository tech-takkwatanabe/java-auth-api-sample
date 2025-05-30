package com.example.api.auth.domain.repository.impl;

import com.example.api.auth.domain.repository.UserRepository;
import com.example.api.auth.mapper.UserMapper;
import com.example.api.auth.model.User;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {

  private final UserMapper userMapper;

  public UserRepositoryImpl(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return Optional.ofNullable(userMapper.selectByUsername(username));
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return Optional.ofNullable(userMapper.selectByEmail(email));
  }

  @Override
  public Optional<User> findByUuid(String uuid) {
    return Optional.ofNullable(userMapper.selectByUuid(uuid));
  }

  @Override
  public boolean existsByUsername(String username) {
    return userMapper.countByUsername(username) > 0;
  }

  @Override
  public boolean existsByEmail(String email) {
    return userMapper.countByEmail(email) > 0;
  }
}
