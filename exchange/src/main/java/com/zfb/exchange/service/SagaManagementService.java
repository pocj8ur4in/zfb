package com.zfb.exchange.service;

import com.zfb.exchange.repository.ExchangeSagaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SagaManagementService {

  private final ExchangeSagaRepository sagaRepository;
  private final ExchangeSagaOrchestrator sagaOrchestrator;
}
