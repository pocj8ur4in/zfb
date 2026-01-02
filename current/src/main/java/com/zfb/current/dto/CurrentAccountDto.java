package com.zfb.current.dto;

import com.zfb.current.domain.CurrentAccount;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CurrentAccountDto {
  private Long id;
  private String accountNumber;
  private Long userId;
  private BigDecimal balance;
  private String status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static CurrentAccountDto from(CurrentAccount account) {
    return CurrentAccountDto.builder()
        .id(account.getId())
        .accountNumber(account.getAccountNumber())
        .userId(account.getUserId())
        .balance(account.getBalance())
        .status(account.getStatus().name())
        .createdAt(account.getCreatedAt())
        .updatedAt(account.getUpdatedAt())
        .build();
  }
}
