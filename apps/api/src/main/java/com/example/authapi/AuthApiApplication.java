package com.example.authapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.authapi.mapper")
public class AuthApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(AuthApiApplication.class, args);
  }
}
