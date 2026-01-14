package com.zfb.exchange.dto;

import com.zfb.domain.Currency;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExchangeRateCompareResponse {

  private Currency sourceCurrency;
  private BigDecimal sourceAmount;
  private List<ComparisonItem> comparisons;

  @Getter
  @Setter
  @NoArgsConstructor
  public static class ComparisonItem {
    private Currency targetCurrency;
    private BigDecimal exchangeRate;
    private BigDecimal spread;
    private BigDecimal effectiveRate;
    private BigDecimal targetAmount;
    private int rank;
  }
}
