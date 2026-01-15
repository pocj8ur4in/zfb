package com.zfb.exchange.consumer;

import com.zfb.exchange.config.KafkaTopicConfig;
import com.zfb.exchange.event.ExchangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RetryEventConsumer {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  @KafkaListener(
      topics = KafkaTopicConfig.EXCHANGE_SAGA_RETRY,
      groupId = "exchange-retry-processor",
      containerFactory = "kafkaListenerContainerFactory")
  public void consumeRetryEvent(
      @Payload ExchangeEvent event,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
      @Header(KafkaHeaders.OFFSET) long offset) {

    log.info(
        "Processing retry event: type={}, sagaId={}, retryCount={}, partition={}, offset={}",
        event.getEventType(),
        event.getSagaId(),
        event.getRetryCount(),
        partition,
        offset);

    try {
      retryPublishEvent(event);
    } catch (Exception e) {
      log.error("Failed to retry event: sagaId={}", event.getSagaId(), e);
      sendBackToDlq(event);
    }
  }

  private void retryPublishEvent(ExchangeEvent event) {
    try {
      kafkaTemplate.send(KafkaTopicConfig.EXCHANGE_SAGA_EVENTS, event.getSagaId(), event);
      log.info(
          "Event republished successfully: sagaId={}, eventType={}",
          event.getSagaId(),
          event.getEventType());
    } catch (Exception e) {
      log.error("Failed to republish event: sagaId={}", event.getSagaId(), e);
      throw e;
    }
  }

  private void sendBackToDlq(ExchangeEvent event) {
    try {
      event.setRetryCount(event.getRetryCount() + 1);
      kafkaTemplate.send(KafkaTopicConfig.EXCHANGE_SAGA_DLQ, event.getSagaId(), event);
      log.warn(
          "Event sent back to DLQ: sagaId={}, retryCount={}",
          event.getSagaId(),
          event.getRetryCount());
    } catch (Exception dlqError) {
      log.error("Failed to send event back to DLQ: sagaId={}", event.getSagaId(), dlqError);
    }
  }
}
