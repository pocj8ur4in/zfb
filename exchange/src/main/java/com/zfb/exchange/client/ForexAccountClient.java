package com.zfb.exchange.client;

import com.zfb.dto.ApiResponse;
import com.zfb.exception.ServiceUnavailableException;
import com.zfb.exchange.dto.AccountDepositRequest;
import com.zfb.exchange.dto.AccountWithdrawRequest;
import com.zfb.exchange.dto.TransactionResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "forex-service",
    url = "${feign.client.config.forex-service.url}",
    path = "/api/forex/accounts")
public interface ForexAccountClient {

  @PostMapping("/{accountUuid}/withdraw")
  @CircuitBreaker(name = "forex-service", fallbackMethod = "withdrawFallback")
  @Retry(name = "forex-service")
  ApiResponse<TransactionResponse> withdraw(
      @PathVariable("accountUuid") String accountUuid, @RequestBody AccountWithdrawRequest request);

  @PostMapping("/{accountUuid}/deposit")
  @CircuitBreaker(name = "forex-service", fallbackMethod = "depositFallback")
  @Retry(name = "forex-service")
  ApiResponse<TransactionResponse> deposit(
      @PathVariable("accountUuid") String accountUuid, @RequestBody AccountDepositRequest request);
}
