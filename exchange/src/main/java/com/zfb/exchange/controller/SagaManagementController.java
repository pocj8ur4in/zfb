package com.zfb.exchange.controller;

import com.zfb.dto.ApiResponse;
import com.zfb.exchange.domain.ExchangeSaga.SagaStatus;
import com.zfb.exchange.dto.SagaDto;
import com.zfb.exchange.service.SagaManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sagas")
@RequiredArgsConstructor
@Slf4j
public class SagaManagementController {

  private final SagaManagementService sagaManagementService;

  @GetMapping
  public ResponseEntity<ApiResponse<Page<SagaDto>>> getAllSagas(Pageable pageable) {
    log.debug("Fetching all sagas with pagination: {}", pageable);
    Page<SagaDto> sagas = sagaManagementService.getAllSagas(pageable);
    return ResponseEntity.ok(ApiResponse.of(sagas));
  }

  @GetMapping("/status/{status}")
  public ResponseEntity<ApiResponse<Page<SagaDto>>> getSagasByStatus(
      @PathVariable String status, Pageable pageable) {
    log.debug("Fetching sagas with status: {}", status);
    SagaStatus sagaStatus = SagaStatus.valueOf(status.toUpperCase());
    Page<SagaDto> sagas = sagaManagementService.getSagasByStatus(sagaStatus, pageable);
    return ResponseEntity.ok(ApiResponse.of(sagas));
  }
}
