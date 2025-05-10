package com.example.authapi.repository.impl;

import com.example.authapi.mapper.UserMapper;
import com.example.authapi.model.User;
import com.example.authapi.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
  public boolean existsByUsername(String username) {
    return userMapper.countByUsername(username) > 0;
  }

  @Override
  public boolean existsByEmail(String email) {
    return userMapper.countByEmail(email) > 0;
  }
}
