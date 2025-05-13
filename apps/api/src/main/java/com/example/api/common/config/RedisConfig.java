package com.example.api.common.config;

import com.example.api.auth.domain.entity.RefreshToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RedisConfig {

  @Bean
  public RedisTemplate<String, RefreshToken> redisTemplate(RedisConnectionFactory factory) {
    RedisTemplate<String, RefreshToken> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);

    // Jackson を使って RefreshToken を JSON 形式で保存
    Jackson2JsonRedisSerializer<RefreshToken> serializer = new Jackson2JsonRedisSerializer<>(RefreshToken.class);

    ObjectMapper mapper = new ObjectMapper();
    mapper.findAndRegisterModules();

    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(serializer);
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(serializer);
    template.setDefaultSerializer(serializer);
    template.afterPropertiesSet();

    return template;
  }
}
