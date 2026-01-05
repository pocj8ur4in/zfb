package com.zfb.current.domain;

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
    name = "current_account_transactions",
    indexes = {
      @Index(name = "idx_account_uuid", columnList = "accountUuid"),
      @Index(name = "idx_client_request_uuid", columnList = "clientRequestUuid"),
      @Index(name = "idx_status_created", columnList = "status,createdAt")
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CurrentAccountTransaction extends BaseColumn {

  @Column(nullable = false)
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

  @Column(name = "client_request_uuid", unique = true, length = 100)
  private String clientRequestUuid;

  @Column(name = "saga_uuid", length = 100)
  private String sagaUuid;

  @Column(length = 500)
  private String description;

  @Builder
  public CurrentAccountTransaction(
      String accountUuid,
      TransactionType type,
      BigDecimal amount,
      BigDecimal balanceBefore,
      BigDecimal balanceAfter,
      TransactionStatus status,
      String clientRequestUuid,
      String sagaUuid,
      String description) {
    this.accountUuid = accountUuid;
    this.type = type;
    this.amount = amount;
    this.balanceBefore = balanceBefore;
    this.balanceAfter = balanceAfter;
    this.status = status != null ? status : TransactionStatus.PENDING;
    this.clientRequestUuid = clientRequestUuid;
    this.sagaUuid = sagaUuid;
    this.description = description;
  }

  /** complete the transaction for current account */
  public void complete() {
    this.status = TransactionStatus.COMPLETED;
  }

  /**
   * fail the transaction for current account
   *
   * @param reason
   */
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
