package com.zfb.current.controller;

import com.zfb.current.dto.*;
import com.zfb.current.service.CurrentAccountService;
import com.zfb.dto.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/current/accounts")
@RequiredArgsConstructor
public class CurrentAccountController {

  private final CurrentAccountService accountService;

  @PostMapping
  public ResponseEntity<ApiResponse<CurrentAccountDto>> createAccount(
      @Valid @RequestBody CreateAccountRequest request) {
    CurrentAccountDto account = accountService.createAccount(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success("account created successfully", account));
  }

  @GetMapping("/{uuid}")
  public ResponseEntity<ApiResponse<CurrentAccountDto>> getAccount(@PathVariable String uuid) {
    CurrentAccountDto account = accountService.getAccount(uuid);
    return ResponseEntity.ok(ApiResponse.success(account));
  }

  @GetMapping("/user/{userUuid}")
  public ResponseEntity<ApiResponse<List<CurrentAccountDto>>> getAccountsByUserUuid(
      @PathVariable String userUuid) {
    List<CurrentAccountDto> accounts = accountService.getAccountsByUserUuid(userUuid);
    return ResponseEntity.ok(ApiResponse.success(accounts));
  }
}
