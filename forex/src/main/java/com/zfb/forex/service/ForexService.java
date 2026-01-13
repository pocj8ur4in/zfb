package com.zfb.forex.service;

import com.zfb.exception.BusinessException;
import com.zfb.forex.domain.ForexAccount;
import com.zfb.forex.domain.ForexTransaction;
import com.zfb.forex.dto.*;
import com.zfb.forex.repository.ForexAccountRepository;
import com.zfb.forex.repository.ForexTransactionRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForexService {

  private final ForexAccountRepository accountRepository;
  private final ForexTransactionRepository transactionRepository;

  @Transactional
  public ForexAccountDto createAccount(CreateAccountRequest request) {
    String accountNumber = generateAccountNumber();

    ForexAccount account =
        ForexAccount.builder()
            .accountNumber(accountNumber)
            .userId(request.getUserId())
            .balance(BigDecimal.ZERO)
            .currency(request.getCurrency())
            .status(ForexAccount.AccountStatus.ACTIVE)
            .build();

    ForexAccount saved = accountRepository.save(account);
    log.info("created forex account: {}", saved.getAccountNumber());

    return ForexAccountDto.from(saved);
  }

  @Transactional(readOnly = true)
  public ForexAccountDto getAccount(String uuid) {
    ForexAccount account =
        accountRepository
            .findByUuid(uuid)
            .orElseThrow(() -> new BusinessException("account not found"));

    return ForexAccountDto.from(account);
  }

  @Transactional(readOnly = true)
  public List<ForexAccountDto> getAccountsByUserUuid(String userUuid) {
    return accountRepository.findByUserUuid(userUuid).stream()
        .map(ForexAccountDto::from)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public BigDecimal getBalance(String uuid) {
    ForexAccount account =
        accountRepository
            .findByUuid(uuid)
            .orElseThrow(() -> new BusinessException("account not found"));

    return account.getBalance();
  }

  @Transactional
  public ForexTransactionDto withdraw(String uuid, WithdrawRequest request) {
    String clientRequestId = request.getClientRequestId();

    if (clientRequestId != null) {
      ForexTransaction existing =
          transactionRepository.findByClientRequestId(clientRequestId).orElse(null);
      if (existing != null) {
        log.info("duplicate withdraw request detected: {}", clientRequestId);
        return ForexTransactionDto.from(existing);
      }
    }

    ForexAccount account =
        accountRepository
            .findByUuidForUpdate(uuid)
            .orElseThrow(() -> new BusinessException("account not found"));

    BigDecimal balanceBefore = account.getBalance();

    try {
      account.withdraw(request.getAmount());

      ForexTransaction transaction =
          ForexTransaction.builder()
              .accountUuid(account.getUuid())
              .type(ForexTransaction.TransactionType.WITHDRAW)
              .amount(request.getAmount())
              .balanceBefore(balanceBefore)
              .balanceAfter(account.getBalance())
              .status(ForexTransaction.TransactionStatus.COMPLETED)
              .clientRequestId(clientRequestId)
              .sagaId(request.getSagaId())
              .description(request.getDescription())
              .build();

      transaction.complete();
      ForexTransaction saved = transactionRepository.save(transaction);

      log.info(
          "withdraw completed: accountUuid={}, amount={}, txUuid={}",
          uuid,
          request.getAmount(),
          saved.getUuid());

      return ForexTransactionDto.from(saved);

    } catch (Exception e) {
      log.error("withdraw failed: accountUuid={}, amount={}", uuid, request.getAmount(), e);

      ForexTransaction failedTransaction =
          ForexTransaction.builder()
              .accountUuid(account.getUuid())
              .type(ForexTransaction.TransactionType.WITHDRAW)
              .amount(request.getAmount())
              .balanceBefore(balanceBefore)
              .balanceAfter(balanceBefore)
              .status(ForexTransaction.TransactionStatus.FAILED)
              .clientRequestId(clientRequestId)
              .sagaId(request.getSagaId())
              .description(request.getDescription())
              .build();

      failedTransaction.fail(e.getMessage());
      transactionRepository.save(failedTransaction);

      throw e;
    }
  }

  @Transactional
  public ForexTransactionDto deposit(String uuid, DepositRequest request) {
    String clientRequestId = request.getClientRequestId();

    if (clientRequestId != null) {
      ForexTransaction existing =
          transactionRepository.findByClientRequestId(clientRequestId).orElse(null);
      if (existing != null) {
        log.info("duplicate deposit request detected: {}", clientRequestId);
        return ForexTransactionDto.from(existing);
      }
    }

    ForexAccount account =
        accountRepository
            .findByUuidForUpdate(uuid)
            .orElseThrow(() -> new BusinessException("account not found"));

    BigDecimal balanceBefore = account.getBalance();

    try {
      account.deposit(request.getAmount());

      ForexTransaction transaction =
          ForexTransaction.builder()
              .accountUuid(account.getUuid())
              .type(ForexTransaction.TransactionType.DEPOSIT)
              .amount(request.getAmount())
              .balanceBefore(balanceBefore)
              .balanceAfter(account.getBalance())
              .status(ForexTransaction.TransactionStatus.COMPLETED)
              .clientRequestId(clientRequestId)
              .sagaId(request.getSagaId())
              .description(request.getDescription())
              .build();

      transaction.complete();
      ForexTransaction saved = transactionRepository.save(transaction);

      log.info(
          "deposit completed: accountUuid={}, amount={}, txUuid={}",
          uuid,
          request.getAmount(),
          saved.getUuid());

      return ForexTransactionDto.from(saved);

    } catch (Exception e) {
      log.error("deposit failed: accountUuid={}, amount={}", uuid, request.getAmount(), e);

      ForexTransaction failedTransaction =
          ForexTransaction.builder()
              .accountUuid(account.getUuid())
              .type(ForexTransaction.TransactionType.DEPOSIT)
              .amount(request.getAmount())
              .balanceBefore(balanceBefore)
              .balanceAfter(balanceBefore)
              .status(ForexTransaction.TransactionStatus.FAILED)
              .clientRequestId(clientRequestId)
              .sagaId(request.getSagaId())
              .description(request.getDescription())
              .build();

      failedTransaction.fail(e.getMessage());
      transactionRepository.save(failedTransaction);

      throw e;
    }
  }

  @Transactional
  public ForexTransactionDto refund(String transactionUuid, String reason) {
    ForexTransaction originalTransaction =
        transactionRepository
            .findByUuid(transactionUuid)
            .orElseThrow(() -> new BusinessException("transaction not found"));

    if (originalTransaction.getType() != ForexTransaction.TransactionType.WITHDRAW) {
      throw new BusinessException("only withdraw transactions can be refunded");
    }

    if (originalTransaction.getStatus() != ForexTransaction.TransactionStatus.COMPLETED) {
      throw new BusinessException("only completed transactions can be refunded");
    }

    ForexAccount account =
        accountRepository
            .findByUuidForUpdate(originalTransaction.getAccountUuid())
            .orElseThrow(() -> new BusinessException("account not found"));

    BigDecimal balanceBefore = account.getBalance();
    account.deposit(originalTransaction.getAmount());

    ForexTransaction refundTransaction =
        ForexTransaction.builder()
            .accountUuid(account.getUuid())
            .type(ForexTransaction.TransactionType.REFUND)
            .amount(originalTransaction.getAmount())
            .balanceBefore(balanceBefore)
            .balanceAfter(account.getBalance())
            .status(ForexTransaction.TransactionStatus.COMPLETED)
            .sagaId(originalTransaction.getSagaId())
            .description("refund for transaction " + transactionUuid + ": " + reason)
            .build();

    refundTransaction.complete();
    ForexTransaction saved = transactionRepository.save(refundTransaction);

    log.info(
        "refund completed: originalTxUuid={}, refundTxUuid={}", transactionUuid, saved.getUuid());

    return ForexTransactionDto.from(saved);
  }

  @Transactional(readOnly = true)
  public Page<ForexTransactionDto> getTransactionHistory(
      String accountUuid, Pageable pageable) {
    return transactionRepository
        .findByAccountUuidOrderByCreatedAtDesc(accountUuid, pageable)
        .map(ForexTransactionDto::from);
  }

  private String generateAccountNumber() {
    String prefix = "FX";
    String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    return prefix + uuid;
  }
}
