package com.zfb.exchange.controller;

import com.zfb.dto.ApiResponse;
import com.zfb.exchange.dto.ExchangeResponse;
import com.zfb.exchange.service.ExchangeRateService;
import com.zfb.exchange.service.ExchangeService;
import lombok.RequiredArgsConstructor;
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
}
