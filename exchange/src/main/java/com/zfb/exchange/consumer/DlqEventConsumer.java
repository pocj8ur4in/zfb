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
public class DlqEventConsumer {

  private static final int MAX_RETRY_COUNT = 3;
  private final KafkaTemplate<String, Object> kafkaTemplate;

  @KafkaListener(
      topics = KafkaTopicConfig.EXCHANGE_SAGA_DLQ,
      groupId = "exchange-dlq-processor",
      containerFactory = "kafkaListenerContainerFactory")
  public void consumeDlqEvent(
      @Payload ExchangeEvent event,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
      @Header(KafkaHeaders.OFFSET) long offset) {

    log.warn(
        "Processing DLQ event: type={}, sagaId={}, retryCount={}, partition={}, offset={}",
        event.getEventType(),
        event.getSagaId(),
        event.getRetryCount(),
        partition,
        offset);

    if (event.getRetryCount() >= MAX_RETRY_COUNT) {
      log.error(
          "Event exceeded max retry count ({}): sagaId={}, eventType={}. Manual intervention required.",
          MAX_RETRY_COUNT,
          event.getSagaId(),
          event.getEventType());
      return;
    }

    try {
      Thread.sleep(calculateBackoffDelay(event.getRetryCount()));
      sendToRetryTopic(event);
    } catch (Exception e) {
      log.error("Failed to process DLQ event: sagaId={}", event.getSagaId(), e);
    }
  }

  private void sendToRetryTopic(ExchangeEvent event) {
    try {
      kafkaTemplate.send(KafkaTopicConfig.EXCHANGE_SAGA_RETRY, event.getSagaId(), event);
      log.info(
          "Event sent to retry topic: sagaId={}, retryCount={}",
          event.getSagaId(),
          event.getRetryCount());
    } catch (Exception e) {
      log.error("Failed to send event to retry topic: sagaId={}", event.getSagaId(), e);
    }
  }

  private long calculateBackoffDelay(int retryCount) {
    long baseDelay = 5000;
    return baseDelay * (long) Math.pow(2, retryCount);
  }
}
