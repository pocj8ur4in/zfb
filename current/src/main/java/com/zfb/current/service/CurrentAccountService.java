package com.zfb.current.service;

import com.zfb.current.domain.CurrentAccount;
import com.zfb.current.domain.CurrentAccountTransaction;
import com.zfb.current.dto.*;
import com.zfb.current.repository.CurrentAccountRepository;
import com.zfb.current.repository.CurrentTransactionRepository;
import com.zfb.exception.BusinessException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrentAccountService {

  private final CurrentAccountRepository accountRepository;
  private final CurrentTransactionRepository transactionRepository;

  /**
   * create a new current account
   *
   * @param request
   * @return
   */
  @Transactional
  public CurrentAccountDto createAccount(CreateAccountRequest request) {
    String accountNumber = generateAccountNumber();

    CurrentAccount account =
        CurrentAccount.builder()
            .accountNumber(accountNumber)
            .userUuid(request.getUserUuid())
            .balance(BigDecimal.ZERO)
            .status(CurrentAccount.AccountStatus.ACTIVE)
            .build();

    CurrentAccount saved = accountRepository.save(account);
    log.info("created current account: {}", saved.getAccountNumber());

    return CurrentAccountDto.from(saved);
  }

  /**
   * get a current account by uuid
   *
   * @param uuid
   * @return
   */
  @Transactional(readOnly = true)
  public CurrentAccountDto getAccount(String uuid) {
    CurrentAccount account =
        accountRepository
            .findByUuid(uuid)
            .orElseThrow(() -> new BusinessException("account not found"));

    return CurrentAccountDto.from(account);
  }

  /**
   * get a list of current accounts by user uuid
   *
   * @param userUuid
   * @return
   */
  @Transactional(readOnly = true)
  public List<CurrentAccountDto> getAccountsByUserUuid(String userUuid) {
    return accountRepository.findByUserUuid(userUuid).stream()
        .map(CurrentAccountDto::from)
        .collect(Collectors.toList());
  }

  /**
   * generate a new account number
   *
   * @return
   */
  private String generateAccountNumber() {
    Random random = new Random();
    StringBuilder accountNumber = new StringBuilder("100");

    // generate 7 random digits
    int[] digits = new int[7];
    for (int i = 0; i < 7; i++) {
      digits[i] = random.nextInt(10);
    }

    // append digits to account number
    accountNumber.append(digits[0]).append("-");
    for (int i = 1; i < 5; i++) {
      accountNumber.append(digits[i]);
    }
    accountNumber.append("-");
    for (int i = 5; i < 7; i++) {
      accountNumber.append(digits[i]);
    }

    // calculate checksum
    String digitsOnly = accountNumber.toString().replace("-", "");
    int[] weights = {3, 7, 1, 3, 7, 1, 3, 7, 1, 3};
    int sum = 0;
    for (int i = 0; i < digitsOnly.length(); i++) {
      sum += Character.getNumericValue(digitsOnly.charAt(i)) * weights[i];
    }
    int checksum = (10 - (sum % 10)) % 10;

    return accountNumber.append(checksum).toString();
  }

  /**
   * withdraw money from account
   *
   * @param uuid account uuid
   * @param request withdraw request
   * @return transaction dto
   */
  @Transactional
  public CurrentTransactionDto withdraw(String uuid, WithdrawRequest request) {
    CurrentAccount account =
        accountRepository
            .findByUuidWithLock(uuid)
            .orElseThrow(() -> new BusinessException("account not found"));

    BigDecimal balanceBefore = account.getBalance();

    // create transaction record
    CurrentAccountTransaction transaction =
        CurrentAccountTransaction.builder()
            .accountUuid(account.getUuid())
            .type(CurrentAccountTransaction.TransactionType.WITHDRAW)
            .amount(request.getAmount())
            .balanceBefore(balanceBefore)
            .status(CurrentAccountTransaction.TransactionStatus.PENDING)
            .clientRequestUuid(request.getClientRequestUuid())
            .sagaUuid(request.getSagaUuid())
            .description(request.getDescription())
            .build();

    try {
      // withdraw from account
      account.withdraw(request.getAmount());

      // update transaction
      transaction.complete();
      BigDecimal balanceAfter = account.getBalance();
      transaction =
          CurrentAccountTransaction.builder()
              .accountUuid(transaction.getAccountUuid())
              .type(transaction.getType())
              .amount(transaction.getAmount())
              .balanceBefore(transaction.getBalanceBefore())
              .balanceAfter(balanceAfter)
              .status(transaction.getStatus())
              .clientRequestUuid(transaction.getClientRequestUuid())
              .sagaUuid(transaction.getSagaUuid())
              .description(transaction.getDescription())
              .build();

      CurrentAccountTransaction saved = transactionRepository.save(transaction);
      log.info(
          "withdraw completed: account={}, amount={}, balance={} -> {}",
          account.getAccountNumber(),
          request.getAmount(),
          balanceBefore,
          balanceAfter);

      return CurrentTransactionDto.from(saved);
    } catch (Exception e) {
      transaction.fail(e.getMessage());
      transactionRepository.save(transaction);
      log.error("withdraw failed: {}", e.getMessage());
      throw new BusinessException(e.getMessage());
    }
  }

  /**
   * deposit money to account
   *
   * @param uuid account uuid
   * @param request deposit request
   * @return transaction dto
   */
  @Transactional
  public CurrentTransactionDto deposit(String uuid, DepositRequest request) {
    CurrentAccount account =
        accountRepository
            .findByUuidWithLock(uuid)
            .orElseThrow(() -> new BusinessException("account not found"));

    BigDecimal balanceBefore = account.getBalance();

    // create transaction record
    CurrentAccountTransaction transaction =
        CurrentAccountTransaction.builder()
            .accountUuid(account.getUuid())
            .type(CurrentAccountTransaction.TransactionType.DEPOSIT)
            .amount(request.getAmount())
            .balanceBefore(balanceBefore)
            .status(CurrentAccountTransaction.TransactionStatus.PENDING)
            .clientRequestUuid(request.getClientRequestUuid())
            .sagaUuid(request.getSagaUuid())
            .description(request.getDescription())
            .build();

    try {
      // deposit to account
      account.deposit(request.getAmount());

      // update transaction
      transaction.complete();
      BigDecimal balanceAfter = account.getBalance();
      transaction =
          CurrentAccountTransaction.builder()
              .accountUuid(transaction.getAccountUuid())
              .type(transaction.getType())
              .amount(transaction.getAmount())
              .balanceBefore(transaction.getBalanceBefore())
              .balanceAfter(balanceAfter)
              .status(transaction.getStatus())
              .clientRequestUuid(transaction.getClientRequestUuid())
              .sagaUuid(transaction.getSagaUuid())
              .description(transaction.getDescription())
              .build();

      CurrentAccountTransaction saved = transactionRepository.save(transaction);
      log.info(
          "deposit completed: account={}, amount={}, balance={} -> {}",
          account.getAccountNumber(),
          request.getAmount(),
          balanceBefore,
          balanceAfter);

      return CurrentTransactionDto.from(saved);
    } catch (Exception e) {
      transaction.fail(e.getMessage());
      transactionRepository.save(transaction);
      log.error("deposit failed: {}", e.getMessage());
      throw new BusinessException(e.getMessage());
    }
  }
}
