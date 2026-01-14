package com.zfb.exchange.domain;

import com.zfb.domain.BaseColumn;
import com.zfb.domain.Currency;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(
    name = "exchange_transactions",
    indexes = {
      @Index(name = "idx_client_request_id", columnList = "clientRequestId", unique = true),
      @Index(name = "idx_saga_id", columnList = "sagaId"),
      @Index(name = "idx_account_uuid", columnList = "accountUuid"),
      @Index(name = "idx_status", columnList = "status"),
      @Index(name = "idx_created_at", columnList = "createdAt")
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExchangeTransaction extends BaseColumn {

  @Column(nullable = false, unique = true, length = 100)
  private String clientRequestId;

  @Column(nullable = false, length = 36)
  private String sagaId;

  @Column(nullable = false, length = 36)
  private String accountUuid;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 3)
  private Currency sourceCurrency;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 3)
  private Currency targetCurrency;

  @Column(nullable = false, precision = 20, scale = 2)
  private BigDecimal sourceAmount;

  @Column(nullable = false, precision = 20, scale = 2)
  private BigDecimal targetAmount;

  @Column(nullable = false, precision = 20, scale = 6)
  private BigDecimal appliedRate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ExchangeStatus status;

  @Column(length = 500)
  private String failureReason;

  @Column private LocalDateTime completedAt;

  public ExchangeTransaction(
      String clientRequestId,
      String sagaId,
      String accountUuid,
      Currency sourceCurrency,
      Currency targetCurrency,
      BigDecimal sourceAmount,
      BigDecimal targetAmount,
      BigDecimal appliedRate) {
    this.clientRequestId = clientRequestId;
    this.sagaId = sagaId;
    this.accountUuid = accountUuid;
    this.sourceCurrency = sourceCurrency;
    this.targetCurrency = targetCurrency;
    this.sourceAmount = sourceAmount;
    this.targetAmount = targetAmount;
    this.appliedRate = appliedRate;
    this.status = ExchangeStatus.PENDING;
  }

  public enum ExchangeStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    COMPENSATED
  }

  public void complete() {
    this.status = ExchangeStatus.COMPLETED;
    this.completedAt = LocalDateTime.now();
  }

  public void fail(String reason) {
    this.status = ExchangeStatus.FAILED;
    this.failureReason = reason;
    this.completedAt = LocalDateTime.now();
  }

  public void compensate() {
    this.status = ExchangeStatus.COMPENSATED;
    this.completedAt = LocalDateTime.now();
  }

  public void markProcessing() {
    this.status = ExchangeStatus.PROCESSING;
  }
}
