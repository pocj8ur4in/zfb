package com.zfb.exchange.domain;

import com.zfb.domain.BaseColumn;
import com.zfb.domain.Currency;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(
    name = "exchange_sagas",
    indexes = {
      @Index(name = "idx_saga_id", columnList = "sagaId", unique = true),
      @Index(name = "idx_status", columnList = "status"),
      @Index(name = "idx_created_at", columnList = "createdAt")
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExchangeSaga extends BaseColumn {

  @Column(nullable = false, unique = true, length = 36)
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
  private SagaStatus status;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private SagaStep currentStep;

  @Column(length = 100)
  private String sourceWithdrawTxId;

  @Column(length = 100)
  private String targetDepositTxId;

  @Column(length = 500)
  private String failureReason;

  @Column private LocalDateTime completedAt;

  @Column private LocalDateTime lastRetryAt;

  @Column(nullable = false)
  private int retryCount = 0;

  public ExchangeSaga(
      String sagaId,
      String accountUuid,
      Currency sourceCurrency,
      Currency targetCurrency,
      BigDecimal sourceAmount,
      BigDecimal targetAmount,
      BigDecimal appliedRate) {
    this.sagaId = sagaId;
    this.accountUuid = accountUuid;
    this.sourceCurrency = sourceCurrency;
    this.targetCurrency = targetCurrency;
    this.sourceAmount = sourceAmount;
    this.targetAmount = targetAmount;
    this.appliedRate = appliedRate;
    this.status = SagaStatus.STARTED;
    this.currentStep = SagaStep.WITHDRAW_SOURCE;
    this.retryCount = 0;
  }

  public enum SagaStatus {
    STARTED,
    SOURCE_WITHDRAWN,
    TARGET_DEPOSITED,
    COMPLETED,
    COMPENSATING,
    COMPENSATED,
    FAILED
  }

  public enum SagaStep {
    WITHDRAW_SOURCE,
    DEPOSIT_TARGET,
    COMPLETED,
    COMPENSATE_SOURCE_DEPOSIT,
    COMPENSATE_TARGET_WITHDRAW,
    FAILED
  }

  public void updateStep(SagaStep step, SagaStatus status) {
    this.currentStep = step;
    this.status = status;
  }

  public void recordSourceWithdraw(String txId) {
    this.sourceWithdrawTxId = txId;
    this.currentStep = SagaStep.DEPOSIT_TARGET;
    this.status = SagaStatus.SOURCE_WITHDRAWN;
  }

  public void recordTargetDeposit(String txId) {
    this.targetDepositTxId = txId;
    this.currentStep = SagaStep.COMPLETED;
    this.status = SagaStatus.TARGET_DEPOSITED;
  }

  public void complete() {
    this.status = SagaStatus.COMPLETED;
    this.currentStep = SagaStep.COMPLETED;
    this.completedAt = LocalDateTime.now();
  }

  public void fail(String reason) {
    this.status = SagaStatus.FAILED;
    this.currentStep = SagaStep.FAILED;
    this.failureReason = reason;
    this.completedAt = LocalDateTime.now();
  }

  public void startCompensation() {
    this.status = SagaStatus.COMPENSATING;
    if (this.targetDepositTxId != null) {
      this.currentStep = SagaStep.COMPENSATE_TARGET_WITHDRAW;
    } else {
      this.currentStep = SagaStep.COMPENSATE_SOURCE_DEPOSIT;
    }
  }

  public void markCompensated() {
    this.status = SagaStatus.COMPENSATED;
    this.completedAt = LocalDateTime.now();
  }

  public void incrementRetry() {
    this.retryCount++;
    this.lastRetryAt = LocalDateTime.now();
  }

  public boolean canRetry() {
    return retryCount < 3;
  }
}
