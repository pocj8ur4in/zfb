package com.zfb.exchange.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalExchangeRateResponse {

  private Integer pkid;
  private Integer count;
  private List<CountryRate> country;
  private String calculatorMessage;

  @Getter
  @NoArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class CountryRate {
    private String value;
    private String subValue;
    private String currencyUnit;
  }
}
