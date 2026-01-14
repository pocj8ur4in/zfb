package com.zfb.exchange.client;

import com.zfb.domain.Currency;
import com.zfb.exchange.dto.ExternalExchangeRateResponse;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExternalExchangeRateClient {

  private static final String BASE_URL =
      "https://m.search.naver.com/p/csearch/content/qapirender.nhn";
  private final RestTemplate restTemplate;

  public BigDecimal fetchExchangeRate(Currency source, Currency target) {
    try {
      String url =
          String.format(
              "%s?pkid=141&u3=%s&u4=%s&u2=1", BASE_URL, source.getCode(), target.getCode());

      log.debug("fetching exchange rate from external api: {} -> {}", source, target);

      ExternalExchangeRateResponse response =
          restTemplate.getForObject(url, ExternalExchangeRateResponse.class);

      if (response == null || response.getCountry() == null || response.getCountry().isEmpty()) {
        log.error("invalid response from external api for {} -> {}", source, target);
        throw new IllegalStateException("failed to fetch exchange rate from external api");
      }

      List<ExternalExchangeRateResponse.CountryRate> rates = response.getCountry();
      if (rates.size() < 2) {
        log.error("insufficient rate data from external api for {} -> {}", source, target);
        throw new IllegalStateException("insufficient rate data from external api");
      }

      String rateValue = rates.get(1).getValue().replace(",", "");
      BigDecimal rate = new BigDecimal(rateValue);

      log.info("fetched exchange rate: {} -> {} = {}", source, target, rate);
      return rate;

    } catch (Exception e) {
      log.error("failed to fetch exchange rate for {} -> {}: {}", source, target, e.getMessage());
      throw new RuntimeException("failed to fetch exchange rate from external api", e);
    }
  }
}
