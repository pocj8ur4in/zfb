package com.zfb.exchange.service;

import com.zfb.domain.Currency;
import com.zfb.dto.ApiResponse;
import com.zfb.exchange.client.CurrentAccountClient;
import com.zfb.exchange.client.ForexAccountClient;
import com.zfb.exchange.domain.ExchangeSaga;
import com.zfb.exchange.domain.ExchangeSaga.SagaStatus;
import com.zfb.exchange.domain.ExchangeSaga.SagaStep;
import com.zfb.exchange.dto.AccountDepositRequest;
import com.zfb.exchange.dto.AccountWithdrawRequest;
import com.zfb.exchange.dto.TransactionResponse;
import com.zfb.exchange.repository.ExchangeSagaRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeSagaOrchestrator {

  private final ExchangeSagaRepository sagaRepository;
  private final CurrentAccountClient currentAccountClient;
  private final ForexAccountClient forexAccountClient;
  private final ExchangeEventPublisher eventPublisher;

  @Transactional
  public ExchangeSaga createSaga(
      String accountUuid,
      Currency sourceCurrency,
      Currency targetCurrency,
      BigDecimal sourceAmount,
      BigDecimal targetAmount,
      BigDecimal appliedRate) {

    String sagaId = UUID.randomUUID().toString();

    ExchangeSaga saga =
        new ExchangeSaga(
            sagaId,
            accountUuid,
            sourceCurrency,
            targetCurrency,
            sourceAmount,
            targetAmount,
            appliedRate);

    saga = sagaRepository.save(saga);
    eventPublisher.publishExchangeRequested(saga);
    return saga;
  }

  @Async
  @Transactional
  public void executeSaga(String sagaId) {
    ExchangeSaga saga =
        sagaRepository
            .findBySagaId(sagaId)
            .orElseThrow(() -> new IllegalStateException("saga not found: " + sagaId));

    try {
      if (saga.getCurrentStep() == SagaStep.WITHDRAW_SOURCE) {
        executeWithdrawSource(saga);
      }

      if (saga.getCurrentStep() == SagaStep.DEPOSIT_TARGET) {
        executeDepositTarget(saga);
      }

      if (saga.getStatus() == SagaStatus.TARGET_DEPOSITED) {
        saga.complete();
        sagaRepository.save(saga);
        eventPublisher.publishExchangeCompleted(saga);
        log.info("saga completed successfully: {}", sagaId);
      }

    } catch (Exception e) {
      log.error("saga execution failed: {}", sagaId, e);
      eventPublisher.publishExchangeFailed(saga, e.getMessage());
      compensate(saga, e.getMessage());
    }
  }

  private void executeWithdrawSource(ExchangeSaga saga) {
    String withdrawRequestId = saga.getSagaId() + "-withdraw-source";

    AccountWithdrawRequest withdrawRequest = new AccountWithdrawRequest();
    withdrawRequest.setAmount(saga.getSourceAmount());
    withdrawRequest.setCurrency(saga.getSourceCurrency());
    withdrawRequest.setClientRequestId(withdrawRequestId);
    withdrawRequest.setSagaId(saga.getSagaId());

    ApiResponse<TransactionResponse> response;
    if (saga.getSourceCurrency() == Currency.KRW) {
      response = currentAccountClient.withdraw(saga.getAccountUuid(), withdrawRequest);
    } else {
      response = forexAccountClient.withdraw(saga.getAccountUuid(), withdrawRequest);
    }

    if (response.getData() == null) {
      throw new RuntimeException("Failed to withdraw from source account");
    }

    saga.recordSourceWithdraw(response.getData().getTransactionId());
    sagaRepository.save(saga);
    eventPublisher.publishSourceWithdrawn(saga, response.getData().getTransactionId());
    log.info("Source withdrawal completed for saga: {}", saga.getSagaId());
  }

  private void executeDepositTarget(ExchangeSaga saga) {
    String depositRequestId = saga.getSagaId() + "-deposit-target";

    AccountDepositRequest depositRequest = new AccountDepositRequest();
    depositRequest.setAmount(saga.getTargetAmount());
    depositRequest.setCurrency(saga.getTargetCurrency());
    depositRequest.setClientRequestId(depositRequestId);
    depositRequest.setSagaId(saga.getSagaId());

    ApiResponse<TransactionResponse> response;
    if (saga.getTargetCurrency() == Currency.KRW) {
      response = currentAccountClient.deposit(saga.getAccountUuid(), depositRequest);
    } else {
      response = forexAccountClient.deposit(saga.getAccountUuid(), depositRequest);
    }

    if (response.getData() == null) {
      throw new RuntimeException("Failed to deposit to target account");
    }

    saga.recordTargetDeposit(response.getData().getTransactionId());
    sagaRepository.save(saga);
    eventPublisher.publishTargetDeposited(saga, response.getData().getTransactionId());
    log.info("Target deposit completed for saga: {}", saga.getSagaId());
  }

  @Transactional
  public void compensate(ExchangeSaga saga, String reason) {
    saga.startCompensation();
    sagaRepository.save(saga);
    eventPublisher.publishCompensationStarted(saga);

    try {
      if (saga.getTargetDepositTxId() != null) {
        compensateTargetDeposit(saga);
      }

      if (saga.getSourceWithdrawTxId() != null) {
        compensateSourceWithdraw(saga);
      }

      saga.markCompensated();
      sagaRepository.save(saga);
      eventPublisher.publishCompensationCompleted(saga);
      log.info("Saga compensated successfully: {}", saga.getSagaId());

    } catch (Exception e) {
      log.error("Compensation failed for saga: {}", saga.getSagaId(), e);
      saga.fail("Compensation failed: " + e.getMessage());
      sagaRepository.save(saga);
      eventPublisher.publishExchangeFailed(saga, "Compensation failed: " + e.getMessage());
    }
  }

  private void compensateSourceWithdraw(ExchangeSaga saga) {
    String depositRequestId = saga.getSagaId() + "-compensate-source";

    AccountDepositRequest depositRequest = new AccountDepositRequest();
    depositRequest.setAmount(saga.getSourceAmount());
    depositRequest.setCurrency(saga.getSourceCurrency());
    depositRequest.setClientRequestId(depositRequestId);
    depositRequest.setSagaId(saga.getSagaId());

    ApiResponse<TransactionResponse> response;
    if (saga.getSourceCurrency() == Currency.KRW) {
      response = currentAccountClient.deposit(saga.getAccountUuid(), depositRequest);
    } else {
      response = forexAccountClient.deposit(saga.getAccountUuid(), depositRequest);
    }

    if (response.getData() == null) {
      throw new RuntimeException("Failed to compensate source withdrawal");
    }

    log.info("Source withdrawal compensated for saga: {}", saga.getSagaId());
  }

  private void compensateTargetDeposit(ExchangeSaga saga) {
    String withdrawRequestId = saga.getSagaId() + "-compensate-target";

    AccountWithdrawRequest withdrawRequest = new AccountWithdrawRequest();
    withdrawRequest.setAmount(saga.getTargetAmount());
    withdrawRequest.setCurrency(saga.getTargetCurrency());
    withdrawRequest.setClientRequestId(withdrawRequestId);
    withdrawRequest.setSagaId(saga.getSagaId());

    ApiResponse<TransactionResponse> response;
    if (saga.getTargetCurrency() == Currency.KRW) {
      response = currentAccountClient.withdraw(saga.getAccountUuid(), withdrawRequest);
    } else {
      response = forexAccountClient.withdraw(saga.getAccountUuid(), withdrawRequest);
    }

    if (response.getData() == null) {
      throw new RuntimeException("Failed to compensate target deposit");
    }

    log.info("Target deposit compensated for saga: {}", saga.getSagaId());
  }

  @Scheduled(fixedDelay = 60000)
  @Transactional
  public void recoverStaleSagas() {
    LocalDateTime threshold = LocalDateTime.now().minusMinutes(5);
    List<SagaStatus> statuses =
        List.of(SagaStatus.STARTED, SagaStatus.SOURCE_WITHDRAWN, SagaStatus.COMPENSATING);

    List<ExchangeSaga> staleSagas = sagaRepository.findStaleSagas(statuses, threshold);

    for (ExchangeSaga saga : staleSagas) {
      if (!saga.canRetry()) {
        log.warn("Saga exceeded retry limit: {}", saga.getSagaId());
        compensate(saga, "Exceeded retry limit");
        continue;
      }

      log.info("Retrying stale saga: {}", saga.getSagaId());
      saga.incrementRetry();
      sagaRepository.save(saga);
      executeSaga(saga.getSagaId());
    }
  }
}
