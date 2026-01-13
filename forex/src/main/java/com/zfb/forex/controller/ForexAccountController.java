package com.zfb.forex.controller;

import com.zfb.dto.ApiResponse;
import com.zfb.forex.dto.*;
import com.zfb.forex.service.ForexService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forex/accounts")
@RequiredArgsConstructor
public class ForexAccountController {

  private final ForexService accountService;

  @PostMapping
  public ResponseEntity<ApiResponse<ForexAccountDto>> createAccount(
      @Valid @RequestBody CreateAccountRequest request) {
    ForexAccountDto account = accountService.createAccount(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(account));
  }

  @GetMapping("/{uuid}")
  public ResponseEntity<ApiResponse<ForexAccountDto>> getAccount(@PathVariable String uuid) {
    ForexAccountDto account = accountService.getAccount(uuid);
    return ResponseEntity.ok(ApiResponse.of(account));
  }
}
