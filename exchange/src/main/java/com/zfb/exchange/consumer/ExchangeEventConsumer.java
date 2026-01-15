package com.zfb.exchange.consumer;

import com.zfb.exchange.config.KafkaTopicConfig;
import com.zfb.exchange.event.ExchangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExchangeEventConsumer {

  @KafkaListener(
      topics = KafkaTopicConfig.EXCHANGE_SAGA_EVENTS,
      groupId = "exchange-event-logger",
      containerFactory = "kafkaListenerContainerFactory")
  public void consumeExchangeEvent(
      @Payload ExchangeEvent event,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
      @Header(KafkaHeaders.OFFSET) long offset) {

    log.info(
        "Received exchange event: type={}, sagaId={}, partition={}, offset={}",
        event.getEventType(),
        event.getSagaId(),
        partition,
        offset);

    try {
      processEvent(event);
    } catch (Exception e) {
      log.error("Failed to process exchange event: {}", event.getEventId(), e);
    }
  }

  private void processEvent(ExchangeEvent event) {
    switch (event.getEventType()) {
      case "EXCHANGE_REQUESTED":
        log.info(
            "Exchange requested: sagaId={}, amount={}", event.getSagaId(), event.getSourceAmount());
        break;
      case "SOURCE_WITHDRAWN":
        log.info(
            "Source withdrawn: sagaId={}, txId={}", event.getSagaId(), event.getTransactionId());
        break;
      case "TARGET_DEPOSITED":
        log.info(
            "Target deposited: sagaId={}, txId={}", event.getSagaId(), event.getTransactionId());
        break;
      case "EXCHANGE_COMPLETED":
        log.info("Exchange completed: sagaId={}", event.getSagaId());
        break;
      case "EXCHANGE_FAILED":
        log.warn(
            "Exchange failed: sagaId={}, reason={}", event.getSagaId(), event.getFailureReason());
        break;
      case "COMPENSATION_STARTED":
        log.warn("Compensation started: sagaId={}", event.getSagaId());
        break;
      case "COMPENSATION_COMPLETED":
        log.info("Compensation completed: sagaId={}", event.getSagaId());
        break;
      default:
        log.warn("Unknown event type: {}", event.getEventType());
    }
  }
}
