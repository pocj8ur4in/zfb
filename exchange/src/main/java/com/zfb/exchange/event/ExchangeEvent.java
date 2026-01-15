package com.zfb.exchange.event;

import com.zfb.domain.Currency;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExchangeEvent {
  private String eventId;
  private String eventType;
  private String sagaId;
  private String accountUuid;
  private Currency sourceCurrency;
  private Currency targetCurrency;
  private BigDecimal sourceAmount;
  private BigDecimal targetAmount;
  private BigDecimal appliedRate;
  private String transactionId;
  private String failureReason;
  private LocalDateTime timestamp;
  private int retryCount;

  public enum EventType {
    EXCHANGE_REQUESTED,
    SOURCE_WITHDRAWN,
    TARGET_DEPOSITED,
    EXCHANGE_COMPLETED,
    EXCHANGE_FAILED,
    COMPENSATION_STARTED,
    COMPENSATION_COMPLETED
  }
}
