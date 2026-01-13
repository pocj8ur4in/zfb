package com.zfb.forex.service;

import com.zfb.forex.repository.ForexAccountRepository;
import com.zfb.forex.repository.ForexTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForexService {

  private final ForexAccountRepository accountRepository;
  private final ForexTransactionRepository transactionRepository;
}
