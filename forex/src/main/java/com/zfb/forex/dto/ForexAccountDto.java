package com.zfb.forex.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zfb.forex.domain.ForexAccount;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ForexAccountDto {
  @JsonIgnore private Long id;
  private String uuid;
  private String accountNumber;
  private Long userId;
  private BigDecimal balance;
  private String currency;
  private String status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static ForexAccountDto from(ForexAccount account) {
    return ForexAccountDto.builder()
        .id(account.getId())
        .uuid(account.getUuid())
        .accountNumber(account.getAccountNumber())
        .userId(account.getUserId())
        .balance(account.getBalance())
        .currency(account.getCurrency().name())
        .status(account.getStatus().name())
        .createdAt(account.getCreatedAt())
        .updatedAt(account.getUpdatedAt())
        .build();
  }
}
