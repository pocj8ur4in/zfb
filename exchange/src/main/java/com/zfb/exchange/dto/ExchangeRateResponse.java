package com.zfb.exchange.dto;

import com.zfb.domain.Currency;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExchangeRateResponse {

  private Currency sourceCurrency;
  private Currency targetCurrency;
  private BigDecimal rate;
  private BigDecimal spread;
  private BigDecimal effectiveRate;
  private LocalDateTime effectiveAt;
  private boolean active;
}
