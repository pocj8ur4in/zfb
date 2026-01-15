package com.zfb.exchange.service;

import com.zfb.exchange.config.KafkaTopicConfig;
import com.zfb.exchange.domain.ExchangeSaga;
import com.zfb.exchange.event.ExchangeEvent;
import com.zfb.exchange.event.ExchangeEvent.EventType;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeEventPublisher {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void publishExchangeRequested(ExchangeSaga saga) {
    ExchangeEvent event = buildEvent(saga, EventType.EXCHANGE_REQUESTED, null, null);
    publishEvent(event);
  }

  public void publishExchangeCompleted(ExchangeSaga saga) {
    ExchangeEvent event = buildEvent(saga, EventType.EXCHANGE_COMPLETED, null, null);
    publishEvent(event);
  }

  public void publishExchangeFailed(ExchangeSaga saga, String failureReason) {
    ExchangeEvent event = buildEvent(saga, EventType.EXCHANGE_FAILED, null, failureReason);
    publishEvent(event);
  }

  public void publishSourceWithdrawn(ExchangeSaga saga, String transactionId) {
    ExchangeEvent event = buildEvent(saga, EventType.SOURCE_WITHDRAWN, transactionId, null);
    publishEvent(event);
  }

  public void publishTargetDeposited(ExchangeSaga saga, String transactionId) {
    ExchangeEvent event = buildEvent(saga, EventType.TARGET_DEPOSITED, transactionId, null);
    publishEvent(event);
  }

  public void publishCompensationStarted(ExchangeSaga saga) {
    ExchangeEvent event = buildEvent(saga, EventType.COMPENSATION_STARTED, null, null);
    publishEvent(event);
  }

  public void publishCompensationCompleted(ExchangeSaga saga) {
    ExchangeEvent event = buildEvent(saga, EventType.COMPENSATION_COMPLETED, null, null);
    publishEvent(event);
  }

  private void publishEvent(ExchangeEvent event) {
    try {
      kafkaTemplate
          .send(KafkaTopicConfig.EXCHANGE_SAGA_EVENTS, event.getSagaId(), event)
          .whenComplete(
              (result, ex) -> {
                if (ex != null) {
                  log.error(
                      "Failed to publish event: {} for saga: {}",
                      event.getEventType(),
                      event.getSagaId(),
                      ex);
                  sendToDlq(event, ex);
                } else {
                  log.info(
                      "Event published successfully: {} for saga: {} to partition: {}",
                      event.getEventType(),
                      event.getSagaId(),
                      result.getRecordMetadata().partition());
                }
              });
    } catch (Exception e) {
      log.error("Failed to send event to Kafka: {}", event.getEventType(), e);
      sendToDlq(event, e);
    }
  }

  private void sendToDlq(ExchangeEvent event, Throwable error) {
    try {
      event.setRetryCount(event.getRetryCount() + 1);
      kafkaTemplate.send(KafkaTopicConfig.EXCHANGE_SAGA_DLQ, event.getSagaId(), event);
      log.warn(
          "Event sent to DLQ: {} for saga: {}, error: {}",
          event.getEventType(),
          event.getSagaId(),
          error.getMessage());
    } catch (Exception dlqError) {
      log.error(
          "Failed to send event to DLQ: {} for saga: {}",
          event.getEventType(),
          event.getSagaId(),
          dlqError);
    }
  }

  private ExchangeEvent buildEvent(
      ExchangeSaga saga, EventType eventType, String transactionId, String failureReason) {
    ExchangeEvent event = new ExchangeEvent();
    event.setEventId(UUID.randomUUID().toString());
    event.setEventType(eventType.name());
    event.setSagaId(saga.getSagaId());
    event.setAccountUuid(saga.getAccountUuid());
    event.setSourceCurrency(saga.getSourceCurrency());
    event.setTargetCurrency(saga.getTargetCurrency());
    event.setSourceAmount(saga.getSourceAmount());
    event.setTargetAmount(saga.getTargetAmount());
    event.setAppliedRate(saga.getAppliedRate());
    event.setTransactionId(transactionId);
    event.setFailureReason(failureReason);
    event.setTimestamp(LocalDateTime.now());
    event.setRetryCount(saga.getRetryCount());
    return event;
  }
}
