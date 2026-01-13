package com.zfb.forex.service;

import com.zfb.forex.repository.ForexAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForexAccountService {

  private final ForexAccountRepository accountRepository;
}
