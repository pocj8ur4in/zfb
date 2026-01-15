package com.zfb.exchange.service;

import com.zfb.exchange.domain.ExchangeSaga;
import com.zfb.exchange.dto.SagaDto;
import com.zfb.exchange.repository.ExchangeSagaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SagaManagementService {

  private final ExchangeSagaRepository sagaRepository;
  private final ExchangeSagaOrchestrator sagaOrchestrator;

  @Transactional(readOnly = true)
  public Page<SagaDto> getAllSagas(Pageable pageable) {
    return sagaRepository.findAll(pageable).map(this::toDto);
  }

  private SagaDto toDto(ExchangeSaga saga) {
    SagaDto dto = new SagaDto();
    dto.setId(saga.getId());
    dto.setUuid(saga.getUuid());
    dto.setSagaId(saga.getSagaId());
    dto.setAccountUuid(saga.getAccountUuid());
    dto.setSourceCurrency(saga.getSourceCurrency());
    dto.setTargetCurrency(saga.getTargetCurrency());
    dto.setSourceAmount(saga.getSourceAmount());
    dto.setTargetAmount(saga.getTargetAmount());
    dto.setAppliedRate(saga.getAppliedRate());
    dto.setStatus(saga.getStatus());
    dto.setCurrentStep(saga.getCurrentStep());
    dto.setRetryCount(saga.getRetryCount());
    dto.setFailureReason(saga.getFailureReason());
    dto.setCreatedAt(saga.getCreatedAt());
    dto.setUpdatedAt(saga.getUpdatedAt());
    return dto;
  }
}
