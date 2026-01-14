package com.zfb.exchange.dto;

import com.zfb.domain.Currency;
import com.zfb.exchange.domain.ExchangeTransaction.ExchangeStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExchangeResponse {

  private String clientRequestId;
  private String sagaId;
  private String accountUuid;
  private Currency sourceCurrency;
  private Currency targetCurrency;
  private BigDecimal sourceAmount;
  private BigDecimal targetAmount;
  private BigDecimal appliedRate;
  private ExchangeStatus status;
  private String failureReason;
  private LocalDateTime completedAt;
  private LocalDateTime createdAt;
}
