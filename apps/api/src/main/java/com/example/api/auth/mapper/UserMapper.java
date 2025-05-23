package com.example.api.auth.mapper;

import com.example.api.auth.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
  User selectByUsername(@Param("username") String username);

  User selectByEmail(@Param("email") String email);

  User selectByUuid(@Param("uuid") String uuid);

  int countByUsername(@Param("username") String username);

  int countByEmail(@Param("email") String email);

  void insert(User user);
}
