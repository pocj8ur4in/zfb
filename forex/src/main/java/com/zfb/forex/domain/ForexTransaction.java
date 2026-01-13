package com.zfb.forex.domain;

import com.zfb.domain.BaseColumn;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "forex_transactions",
    indexes = {
      @Index(name = "idx_account_uuid", columnList = "accountUuid"),
      @Index(name = "idx_client_request_id", columnList = "clientRequestId"),
      @Index(name = "idx_status_created", columnList = "status,createdAt")
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ForexTransaction extends BaseColumn {
  @Column(nullable = false, length = 36)
  private String accountUuid;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TransactionType type;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal amount;

  @Column(precision = 19, scale = 2)
  private BigDecimal balanceBefore;

  @Column(precision = 19, scale = 2)
  private BigDecimal balanceAfter;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TransactionStatus status;

  @Column(unique = true, length = 100)
  private String clientRequestId;

  @Column(length = 100)
  private String sagaId;

  @Column(length = 500)
  private String description;

  @Builder
  public ForexTransaction(
      String accountUuid,
      TransactionType type,
      BigDecimal amount,
      BigDecimal balanceBefore,
      BigDecimal balanceAfter,
      TransactionStatus status,
      String clientRequestId,
      String sagaId,
      String description) {
    this.accountUuid = accountUuid;
    this.type = type;
    this.amount = amount;
    this.balanceBefore = balanceBefore;
    this.balanceAfter = balanceAfter;
    this.status = status != null ? status : TransactionStatus.PENDING;
    this.clientRequestId = clientRequestId;
    this.sagaId = sagaId;
    this.description = description;
  }

  public void complete() {
    this.status = TransactionStatus.COMPLETED;
  }

  public void fail(String reason) {
    this.status = TransactionStatus.FAILED;
    this.description =
        (this.description != null ? this.description + " | " : "") + "Failed: " + reason;
  }

  public enum TransactionType {
    DEPOSIT,
    WITHDRAW,
    REFUND
  }

  public enum TransactionStatus {
    PENDING,
    COMPLETED,
    FAILED
  }
}
