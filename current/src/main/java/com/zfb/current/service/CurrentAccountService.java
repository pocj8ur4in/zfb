package com.zfb.current.service;

import com.zfb.current.dto.*;
import com.zfb.current.repository.CurrentAccountRepository;
import com.zfb.current.repository.CurrentTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrentAccountService {

  private final CurrentAccountRepository accountRepository;
  private final CurrentTransactionRepository transactionRepository;
}
