package com.zfb.exchange.service;

import com.zfb.exception.BusinessException;
import com.zfb.exchange.domain.ExchangeTransaction;
import com.zfb.exchange.dto.ExchangeResponse;
import com.zfb.exchange.repository.ExchangeTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeService {

  private final ExchangeTransactionRepository transactionRepository;

  @Transactional(readOnly = true)
  public ExchangeResponse getExchangeStatus(String clientRequestId) {
    ExchangeTransaction transaction =
        transactionRepository
            .findByClientRequestId(clientRequestId)
            .orElseThrow(
                () -> new BusinessException("Exchange transaction not found: " + clientRequestId));

    return toResponse(transaction);
  }

  private ExchangeResponse toResponse(ExchangeTransaction transaction) {
    ExchangeResponse response = new ExchangeResponse();
    response.setClientRequestId(transaction.getClientRequestId());
    response.setSagaId(transaction.getSagaId());
    response.setAccountUuid(transaction.getAccountUuid());
    response.setSourceCurrency(transaction.getSourceCurrency());
    response.setTargetCurrency(transaction.getTargetCurrency());
    response.setSourceAmount(transaction.getSourceAmount());
    response.setTargetAmount(transaction.getTargetAmount());
    response.setAppliedRate(transaction.getAppliedRate());
    response.setStatus(transaction.getStatus());
    response.setFailureReason(transaction.getFailureReason());
    response.setCompletedAt(transaction.getCompletedAt());
    response.setCreatedAt(transaction.getCreatedAt());
    return response;
  }
}
