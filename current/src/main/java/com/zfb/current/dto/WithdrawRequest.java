package com.zfb.current.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WithdrawRequest {

  private String accountUuid;

  @NotNull(message = "amount is required")
  @DecimalMin(value = "0.01", message = "amount must be greater than 0")
  private BigDecimal amount;

  private String clientRequestUuid;

  private String sagaUuid;

  private String description;
}
