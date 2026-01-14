package com.zfb.exchange.domain;

import com.zfb.domain.BaseColumn;
import com.zfb.domain.Currency;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(
    name = "exchange_rates",
    indexes = {
      @Index(name = "idx_currency_pair", columnList = "sourceCurrency, targetCurrency"),
      @Index(name = "idx_effective_at", columnList = "effectiveAt")
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExchangeRate extends BaseColumn {

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 3)
  private Currency sourceCurrency;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 3)
  private Currency targetCurrency;

  @Column(nullable = false, precision = 20, scale = 6)
  private BigDecimal rate;

  @Column(nullable = false, precision = 20, scale = 6)
  private BigDecimal spread;

  @Column(nullable = false)
  private LocalDateTime effectiveAt;

  @Column(nullable = false)
  private boolean active;

  public ExchangeRate(
      Currency sourceCurrency,
      Currency targetCurrency,
      BigDecimal rate,
      BigDecimal spread,
      LocalDateTime effectiveAt,
      boolean active) {
    this.sourceCurrency = sourceCurrency;
    this.targetCurrency = targetCurrency;
    this.rate = rate;
    this.spread = spread;
    this.effectiveAt = effectiveAt;
    this.active = active;
  }

  public BigDecimal getEffectiveRate() {
    return rate.add(spread);
  }

  public void deactivate() {
    this.active = false;
  }
}
