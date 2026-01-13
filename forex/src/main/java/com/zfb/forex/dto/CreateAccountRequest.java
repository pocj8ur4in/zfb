package com.zfb.forex.dto;

import com.zfb.domain.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateAccountRequest {

  @NotNull(message = "user id is required")
  private Long userId;

  @NotNull(message = "currency is required")
  private Currency currency;
}
