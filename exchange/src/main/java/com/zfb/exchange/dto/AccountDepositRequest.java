package com.zfb.exchange.dto;

import com.zfb.domain.Currency;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AccountDepositRequest {

  private BigDecimal amount;
  private Currency currency;
  private String clientRequestId;
  private String sagaId;
}
