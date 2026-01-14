package com.zfb.exchange.dto;

import com.zfb.domain.Currency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExchangeRateCompareRequest {

  @NotNull(message = "source currency is required")
  private Currency sourceCurrency;

  @NotNull(message = "target currencies are required")
  @Size(min = 1, max = 10, message = "target currencies must be between 1 and 10")
  private List<Currency> targetCurrencies;

  @NotNull(message = "amount is required")
  @DecimalMin(value = "0.01", message = "amount must be at least 0.01")
  private BigDecimal amount;
}
