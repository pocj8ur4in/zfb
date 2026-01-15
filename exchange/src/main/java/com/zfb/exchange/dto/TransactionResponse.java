package com.zfb.exchange.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TransactionResponse {

  private String transactionId;
  private String accountUuid;
  private BigDecimal amount;
  private String type;
  private String status;
  private LocalDateTime createdAt;
}
