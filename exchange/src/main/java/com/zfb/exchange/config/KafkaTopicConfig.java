package com.zfb.exchange.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

  public static final String EXCHANGE_SAGA_EVENTS = "exchange.saga.events";
  public static final String EXCHANGE_SAGA_DLQ = "exchange.saga.dlq";
  public static final String EXCHANGE_SAGA_RETRY = "exchange.saga.retry";

  @Bean
  public NewTopic exchangeSagaEventsTopic() {
    return TopicBuilder.name(EXCHANGE_SAGA_EVENTS)
        .partitions(3)
        .replicas(1)
        .config("retention.ms", "604800000")
        .build();
  }

  @Bean
  public NewTopic exchangeSagaDlqTopic() {
    return TopicBuilder.name(EXCHANGE_SAGA_DLQ)
        .partitions(1)
        .replicas(1)
        .config("retention.ms", "2592000000")
        .build();
  }

  @Bean
  public NewTopic exchangeSagaRetryTopic() {
    return TopicBuilder.name(EXCHANGE_SAGA_RETRY)
        .partitions(3)
        .replicas(1)
        .config("retention.ms", "86400000")
        .build();
  }
}
