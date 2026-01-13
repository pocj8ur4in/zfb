package com.zfb.forex.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DepositRequest {

  @NotNull(message = "amount is required")
  @DecimalMin(value = "0.01", message = "amount must be greater than 0")
  private BigDecimal amount;

  private String clientRequestId;

  private String sagaId;

  private String description;
}
