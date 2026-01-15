package com.zfb.exchange.service;

import com.zfb.exception.BusinessException;
import com.zfb.exchange.domain.ExchangeSaga;
import com.zfb.exchange.domain.ExchangeTransaction;
import com.zfb.exchange.domain.ExchangeTransaction.ExchangeStatus;
import com.zfb.exchange.dto.ExchangeRequest;
import com.zfb.exchange.dto.ExchangeResponse;
import com.zfb.exchange.repository.ExchangeTransactionRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeService {

  private final ExchangeTransactionRepository transactionRepository;
  private final ExchangeRateService exchangeRateService;
  private final ExchangeSagaOrchestrator sagaOrchestrator;

  @Transactional
  public ExchangeResponse exchange(ExchangeRequest request) {
    ExchangeTransaction existing =
        transactionRepository.findByClientRequestId(request.getClientRequestId()).orElse(null);

    if (existing != null) {
      log.info("Duplicate exchange request detected: {}", request.getClientRequestId());
      return toResponse(existing);
    }

    if (request.getSourceCurrency() == request.getTargetCurrency()) {
      throw new IllegalArgumentException("Source and target currencies must be different");
    }

    BigDecimal targetAmount =
        exchangeRateService.calculateTargetAmount(
            request.getSourceCurrency(), request.getTargetCurrency(), request.getAmount());

    BigDecimal appliedRate =
        exchangeRateService
            .getLatestRate(request.getSourceCurrency(), request.getTargetCurrency())
            .getEffectiveRate();

    ExchangeSaga saga =
        sagaOrchestrator.createSaga(
            request.getAccountUuid(),
            request.getSourceCurrency(),
            request.getTargetCurrency(),
            request.getAmount(),
            targetAmount,
            appliedRate);

    ExchangeTransaction transaction =
        new ExchangeTransaction(
            request.getClientRequestId(),
            saga.getSagaId(),
            request.getAccountUuid(),
            request.getSourceCurrency(),
            request.getTargetCurrency(),
            request.getAmount(),
            targetAmount,
            appliedRate);

    transactionRepository.save(transaction);

    sagaOrchestrator.executeSaga(saga.getSagaId());

    log.info(
        "Exchange transaction created: {} for saga: {}",
        transaction.getClientRequestId(),
        saga.getSagaId());

    return toResponse(transaction);
  }

  @Transactional(readOnly = true)
  public ExchangeResponse getExchangeStatus(String clientRequestId) {
    ExchangeTransaction transaction =
        transactionRepository
            .findByClientRequestId(clientRequestId)
            .orElseThrow(
                () -> new BusinessException("Exchange transaction not found: " + clientRequestId));

    return toResponse(transaction);
  }

  @Transactional(readOnly = true)
  public Page<ExchangeResponse> getExchangeHistory(String accountUuid, Pageable pageable) {
    Page<ExchangeTransaction> transactions =
        transactionRepository.findByAccountUuid(accountUuid, pageable);
    return transactions.map(this::toResponse);
  }

  @Transactional
  public void updateTransactionStatus(String sagaId, ExchangeStatus status, String failureReason) {
    ExchangeTransaction transaction =
        transactionRepository
            .findBySagaId(sagaId)
            .orElseThrow(
                () -> new BusinessException("Exchange transaction not found for saga: " + sagaId));

    if (status == ExchangeStatus.COMPLETED) {
      transaction.complete();
    } else if (status == ExchangeStatus.FAILED) {
      transaction.fail(failureReason);
    } else if (status == ExchangeStatus.COMPENSATED) {
      transaction.compensate();
    } else {
      transaction.markProcessing();
    }

    transactionRepository.save(transaction);
    log.info("Transaction status updated: {} -> {}", transaction.getClientRequestId(), status);
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
