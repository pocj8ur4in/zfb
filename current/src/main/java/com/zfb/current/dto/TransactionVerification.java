package com.zfb.current.dto;

import com.zfb.current.domain.CurrentAccountTransaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TransactionVerification {
  private String transactionUuid;
  private String clientRequestUuid;
  private String status;
  private String type;
  private BigDecimal amount;
  private String accountUuid;
  private BalanceInfo balance;
  private LocalDateTime timestamp;
  private String sagaUuid;

  @Getter
  @Builder
  public static class BalanceInfo {
    private BigDecimal before;
    private BigDecimal after;
  }

  public static TransactionVerification from(CurrentAccountTransaction transaction) {
    return TransactionVerification.builder()
        .transactionUuid(transaction.getUuid())
        .clientRequestUuid(transaction.getClientRequestUuid())
        .status(transaction.getStatus().name())
        .type(transaction.getType().name())
        .amount(transaction.getAmount())
        .accountUuid(transaction.getAccountUuid())
        .balance(
            BalanceInfo.builder()
                .before(transaction.getBalanceBefore())
                .after(transaction.getBalanceAfter())
                .build())
        .timestamp(transaction.getCreatedAt())
        .sagaUuid(transaction.getSagaUuid())
        .build();
  }

  public static TransactionVerification notFound() {
    return TransactionVerification.builder().status("NOT_FOUND").build();
  }
}
