package com.zfb.forex.service;

import com.zfb.exception.BusinessException;
import com.zfb.forex.domain.ForexAccount;
import com.zfb.forex.dto.*;
import com.zfb.forex.repository.ForexAccountRepository;
import com.zfb.forex.repository.ForexTransactionRepository;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  private String generateAccountNumber() {
    String prefix = "FX";
    String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    return prefix + uuid;
  }
}
