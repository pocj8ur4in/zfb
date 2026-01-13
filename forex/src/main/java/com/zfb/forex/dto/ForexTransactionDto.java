package com.zfb.forex.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zfb.forex.domain.ForexTransaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ForexTransactionDto {
  @JsonIgnore private Long id;
  private String uuid;
  private String accountUuid;
  private String type;
  private BigDecimal amount;
  private BigDecimal balanceBefore;
  private BigDecimal balanceAfter;
  private String status;
  private String description;
  private LocalDateTime createdAt;

  public static ForexTransactionDto from(ForexTransaction transaction) {
    return ForexTransactionDto.builder()
        .id(transaction.getId())
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
