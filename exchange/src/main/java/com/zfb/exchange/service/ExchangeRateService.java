package com.zfb.exchange.service;

import com.zfb.domain.Currency;
import com.zfb.exception.BusinessException;
import com.zfb.exchange.domain.ExchangeRate;
import com.zfb.exchange.dto.ExchangeRateCompareResponse;
import com.zfb.exchange.dto.ExchangeRateResponse;
import com.zfb.exchange.repository.ExchangeRateRepository;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
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

  @Transactional(readOnly = true)
  public ExchangeRateCompareResponse compareRates(
      Currency source, List<Currency> targets, BigDecimal amount) {

    if (targets == null || targets.isEmpty()) {
      throw new IllegalArgumentException("target currencies must not be empty");
    }

    List<Currency> validTargets =
        targets.stream().filter(target -> target != source).distinct().collect(Collectors.toList());

    if (validTargets.isEmpty()) {
      throw new IllegalArgumentException("target currencies must be different from source");
    }

    List<ExchangeRateCompareResponse.ComparisonItem> comparisons =
        validTargets.stream()
            .map(
                target -> {
                  try {
                    ExchangeRate rate =
                        exchangeRateRepository
                            .findLatestRate(source, target)
                            .orElseThrow(
                                () ->
                                    new BusinessException(
                                        String.format(
                                            "exchange rate not found for %s to %s",
                                            source, target)));

                    BigDecimal targetAmount =
                        amount
                            .multiply(rate.getEffectiveRate())
                            .setScale(2, java.math.RoundingMode.DOWN);

                    ExchangeRateCompareResponse.ComparisonItem item =
                        new ExchangeRateCompareResponse.ComparisonItem();
                    item.setTargetCurrency(target);
                    item.setExchangeRate(rate.getRate());
                    item.setSpread(rate.getSpread());
                    item.setEffectiveRate(rate.getEffectiveRate());
                    item.setTargetAmount(targetAmount);
                    return item;

                  } catch (BusinessException e) {
                    log.warn("failed to get rate for {} to {}: {}", source, target, e.getMessage());
                    return null;
                  }
                })
            .filter(item -> item != null)
            .sorted(
                Comparator.comparing(ExchangeRateCompareResponse.ComparisonItem::getTargetAmount)
                    .reversed())
            .collect(Collectors.toList());

    if (comparisons.isEmpty()) {
      throw new BusinessException("no exchange rates found for comparison");
    }

    AtomicInteger rank = new AtomicInteger(1);
    List<ExchangeRateCompareResponse.ComparisonItem> rankedComparisons =
        comparisons.stream()
            .map(
                item -> {
                  ExchangeRateCompareResponse.ComparisonItem rankedItem =
                      new ExchangeRateCompareResponse.ComparisonItem();
                  rankedItem.setTargetCurrency(item.getTargetCurrency());
                  rankedItem.setExchangeRate(item.getExchangeRate());
                  rankedItem.setSpread(item.getSpread());
                  rankedItem.setEffectiveRate(item.getEffectiveRate());
                  rankedItem.setTargetAmount(item.getTargetAmount());
                  rankedItem.setRank(rank.getAndIncrement());
                  return rankedItem;
                })
            .collect(Collectors.toList());

    ExchangeRateCompareResponse response = new ExchangeRateCompareResponse();
    response.setSourceCurrency(source);
    response.setSourceAmount(amount);
    response.setComparisons(rankedComparisons);
    return response;
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
