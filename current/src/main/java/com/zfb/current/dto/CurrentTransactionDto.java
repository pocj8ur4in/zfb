package com.zfb.current.dto;

import com.zfb.current.domain.CurrentAccountTransaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CurrentTransactionDto {
  private String uuid;
  private String accountUuid;
  private String type;
  private BigDecimal amount;
  private BigDecimal balanceBefore;
  private BigDecimal balanceAfter;
  private String status;
  private String description;
  private LocalDateTime createdAt;

  public static CurrentTransactionDto from(CurrentAccountTransaction transaction) {
    return CurrentTransactionDto.builder()
        .uuid(transaction.getUuid())
        .accountUuid(transaction.getAccountUuid())
        .type(transaction.getType().name())
        .amount(transaction.getAmount())
        .balanceBefore(transaction.getBalanceBefore())
        .balanceAfter(transaction.getBalanceAfter())
        .status(transaction.getStatus().name())
        .description(transaction.getDescription())
        .createdAt(transaction.getCreatedAt())
        .build();
  }
}
