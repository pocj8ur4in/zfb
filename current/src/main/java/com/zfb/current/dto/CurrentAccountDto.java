package com.zfb.current.dto;

import com.zfb.current.domain.CurrentAccount;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CurrentAccountDto {
  private String uuid;
  private String accountNumber;
  private String userUuid;
  private BigDecimal balance;
  private String status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static CurrentAccountDto from(CurrentAccount account) {
    return CurrentAccountDto.builder()
        .uuid(account.getUuid())
        .accountNumber(account.getAccountNumber())
        .userUuid(account.getUserUuid())
        .balance(account.getBalance())
        .status(account.getStatus().name())
        .createdAt(account.getCreatedAt())
        .updatedAt(account.getUpdatedAt())
        .build();
  }
}
