package com.zfb.exchange.controller;

import com.zfb.exchange.service.SagaManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sagas")
@RequiredArgsConstructor
@Slf4j
public class SagaManagementController {

  private final SagaManagementService sagaManagementService;
}
