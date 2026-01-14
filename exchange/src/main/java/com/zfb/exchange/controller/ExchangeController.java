package com.zfb.exchange.controller;

import com.zfb.domain.Currency;
import com.zfb.dto.ApiResponse;
import com.zfb.exchange.dto.ExchangeRateCompareRequest;
import com.zfb.exchange.dto.ExchangeRateCompareResponse;
import com.zfb.exchange.dto.ExchangeRateResponse;
import com.zfb.exchange.dto.ExchangeResponse;
import com.zfb.exchange.service.ExchangeRateService;
import com.zfb.exchange.service.ExchangeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/exchange")
@RequiredArgsConstructor
public class ExchangeController {

  private final ExchangeService exchangeService;
  private final ExchangeRateService exchangeRateService;

  @GetMapping("/{clientRequestId}")
  public ApiResponse<ExchangeResponse> getExchangeStatus(@PathVariable String clientRequestId) {
    ExchangeResponse response = exchangeService.getExchangeStatus(clientRequestId);
    return ApiResponse.of(response);
  }

  @GetMapping("/history/{accountUuid}")
  public ApiResponse<Page<ExchangeResponse>> getExchangeHistory(
      @PathVariable String accountUuid,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    Page<ExchangeResponse> response = exchangeService.getExchangeHistory(accountUuid, pageable);
    return ApiResponse.of(response);
  }

  @GetMapping("/rates/{source}/{target}")
  public ApiResponse<ExchangeRateResponse> getExchangeRate(
      @PathVariable Currency source, @PathVariable Currency target) {
    ExchangeRateResponse response = exchangeRateService.getLatestRate(source, target);
    return ApiResponse.of(response);
  }

  @PostMapping("/rates/compare")
  public ApiResponse<ExchangeRateCompareResponse> compareExchangeRates(
      @Valid @RequestBody ExchangeRateCompareRequest request) {
    ExchangeRateCompareResponse response =
        exchangeRateService.compareRates(
            request.getSourceCurrency(), request.getTargetCurrencies(), request.getAmount());
    return ApiResponse.of(response);
  }
}
