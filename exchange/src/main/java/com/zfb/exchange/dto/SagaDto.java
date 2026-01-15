package com.zfb.exchange.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zfb.domain.Currency;
import com.zfb.exchange.domain.ExchangeSaga.SagaStatus;
import com.zfb.exchange.domain.ExchangeSaga.SagaStep;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SagaDto {
  @JsonIgnore private Long id;
  private String uuid;
  private String sagaId;
  private String accountUuid;
  private Currency sourceCurrency;
  private Currency targetCurrency;
  private BigDecimal sourceAmount;
  private BigDecimal targetAmount;
  private BigDecimal appliedRate;
  private SagaStatus status;
  private SagaStep currentStep;
  private Integer retryCount;
  private String failureReason;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
