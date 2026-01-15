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
    name = "current-service",
    url = "${feign.client.config.current-service.url}",
    path = "/api/current/accounts")
public interface CurrentAccountClient {

  @PostMapping("/{accountUuid}/withdraw")
  @CircuitBreaker(name = "current-service", fallbackMethod = "withdrawFallback")
  @Retry(name = "current-service")
  ApiResponse<TransactionResponse> withdraw(
      @PathVariable("accountUuid") String accountUuid, @RequestBody AccountWithdrawRequest request);

  @PostMapping("/{accountUuid}/deposit")
  @CircuitBreaker(name = "current-service", fallbackMethod = "depositFallback")
  @Retry(name = "current-service")
  ApiResponse<TransactionResponse> deposit(
      @PathVariable("accountUuid") String accountUuid, @RequestBody AccountDepositRequest request);
}
