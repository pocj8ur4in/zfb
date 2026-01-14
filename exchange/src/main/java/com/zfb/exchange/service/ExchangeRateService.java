package com.zfb.exchange.service;

import com.zfb.domain.Currency;
import com.zfb.exception.BusinessException;
import com.zfb.exchange.domain.ExchangeRate;
import com.zfb.exchange.dto.ExchangeRateResponse;
import com.zfb.exchange.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateService {

  private final ExchangeRateRepository exchangeRateRepository;

  @Cacheable(value = "exchangeRates", key = "#source.code + '_' + #target.code")
  @Transactional(readOnly = true)
  public ExchangeRateResponse getLatestRate(Currency source, Currency target) {
    if (source == target) {
      throw new IllegalArgumentException("source and target currencies must be different");
    }

    ExchangeRate rate =
        exchangeRateRepository
            .findLatestRate(source, target)
            .orElseThrow(
                () ->
                    new BusinessException(
                        String.format("exchange rate not found for %s to %s", source, target)));

    return toResponse(rate);
  }

  private ExchangeRateResponse toResponse(ExchangeRate rate) {
    ExchangeRateResponse response = new ExchangeRateResponse();
    response.setSourceCurrency(rate.getSourceCurrency());
    response.setTargetCurrency(rate.getTargetCurrency());
    response.setRate(rate.getRate());
    response.setSpread(rate.getSpread());
    response.setEffectiveRate(rate.getEffectiveRate());
    response.setEffectiveAt(rate.getEffectiveAt());
    response.setActive(rate.isActive());
    return response;
  }
}
