package com.zfb.forex.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zfb.forex.domain.ForexTransaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TransactionVerification {
  @JsonIgnore private Long transactionId;
  private String transactionUuid;
  private String clientRequestId;
  private String status;
  private String type;
  private BigDecimal amount;
  private String accountUuid;
  private BalanceInfo balance;
  private LocalDateTime timestamp;
  private String sagaId;

  @Getter
  @Builder
  public static class BalanceInfo {
    private BigDecimal before;
    private BigDecimal after;
  }

  public static TransactionVerification from(ForexTransaction transaction) {
    return TransactionVerification.builder()
        .transactionId(transaction.getId())
        .transactionUuid(transaction.getUuid())
        .clientRequestId(transaction.getClientRequestId())
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
        .sagaId(transaction.getSagaId())
        .build();
  }

  public static TransactionVerification notFound() {
    return TransactionVerification.builder().status("NOT_FOUND").build();
  }
}
