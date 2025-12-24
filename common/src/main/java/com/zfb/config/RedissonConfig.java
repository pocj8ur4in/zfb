package com.zfb.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@EnableAutoConfiguration(exclude = {RedissonAutoConfigurationV2.class})
public class RedissonConfig {

  @Value("${spring.data.redis.host:localhost}")
  private String redisHost;

  @Value("${spring.data.redis.port:6379}")
  private int redisPort;

  @Value("${spring.data.redis.password:}")
  private String redisPassword;

  @Value("${spring.data.redis.database:0}")
  private int redisDatabase;

  @Bean
  public RedissonClient redissonClient() {
    Config config = new Config();

    config
        .useSingleServer()
        .setAddress(String.format("redis://%s:%d", redisHost, redisPort))
        .setDatabase(redisDatabase)
        .setConnectionPoolSize(64)
        .setConnectionMinimumIdleSize(10)
        .setIdleConnectionTimeout(10000)
        .setConnectTimeout(10000)
        .setTimeout(3000)
        .setRetryAttempts(3)
        .setRetryInterval(1500);

    if (redisPassword != null && !redisPassword.isEmpty()) {
      config.useSingleServer().setPassword(redisPassword);
    }

    return Redisson.create(config);
  }

  @Bean
  public RedisConnectionFactory redissonConnectionFactory(RedissonClient redissonClient) {
    return new RedissonConnectionFactory(redissonClient);
  }
}
