package com.example.api.common.config;

import com.example.api.auth.domain.entity.RefreshToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

@Configuration
public class RedisConfig {

  private final ObjectMapper objectMapper;

  public RedisConfig(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @SuppressWarnings("null")
  @Bean
  public RedisTemplate<String, RefreshToken> redisTemplate(RedisConnectionFactory factory) {
    RedisTemplate<String, RefreshToken> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);

    // Jackson を使って RefreshToken を JSON 形式で保存
    // ここで、注入されたカスタマイズ済みの objectMapper を使用する
    Jackson2JsonRedisSerializer<RefreshToken> serializer = new Jackson2JsonRedisSerializer<>(objectMapper,
        RefreshToken.class);

    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(serializer);
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(serializer);
    template.setDefaultSerializer(serializer);
    template.afterPropertiesSet();

    return template;
  }
}
