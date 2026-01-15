package com.zfb.exchange.dto;

import com.zfb.domain.Currency;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExchangeRequest {

  @NotNull(message = "client request id is required")
  @Size(min = 1, max = 100, message = "client request id must be between 1 and 100 characters")
  private String clientRequestId;

  @NotNull(message = "account uuid is required")
  @Size(min = 36, max = 36, message = "account uuid must be 36 characters")
  private String accountUuid;

  @NotNull(message = "source currency is required")
  private Currency sourceCurrency;

  @NotNull(message = "target currency is required")
  private Currency targetCurrency;

  @NotNull(message = "amount is required")
  @DecimalMin(value = "0.01", message = "amount must be at least 0.01")
  @Digits(integer = 18, fraction = 2, message = "invalid amount format")
  private BigDecimal amount;
}
