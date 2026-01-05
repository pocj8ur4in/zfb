package com.zfb.current.controller;

import com.zfb.current.dto.*;
import com.zfb.current.service.CurrentAccountService;
import com.zfb.dto.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

  @PostMapping("/{uuid}/withdraw")
  public ResponseEntity<ApiResponse<CurrentTransactionDto>> withdraw(
      @PathVariable String uuid, @Valid @RequestBody WithdrawRequest request) {
    CurrentTransactionDto transaction = accountService.withdraw(uuid, request);
    return ResponseEntity.ok(ApiResponse.success("withdraw completed successfully", transaction));
  }

  @PostMapping("/{uuid}/deposit")
  public ResponseEntity<ApiResponse<CurrentTransactionDto>> deposit(
      @PathVariable String uuid, @Valid @RequestBody DepositRequest request) {
    CurrentTransactionDto transaction = accountService.deposit(uuid, request);
    return ResponseEntity.ok(ApiResponse.success("deposit completed successfully", transaction));
  }

  @PostMapping("/transactions/{transactionUuid}/refund")
  public ResponseEntity<ApiResponse<CurrentTransactionDto>> refund(
      @PathVariable String transactionUuid, @RequestParam String reason) {
    CurrentTransactionDto transaction = accountService.refund(transactionUuid, reason);
    return ResponseEntity.ok(ApiResponse.success("refund completed", transaction));
  }

  @GetMapping("/{uuid}/transactions")
  public ResponseEntity<ApiResponse<Page<CurrentTransactionDto>>> getTransactionHistory(
      @PathVariable String uuid, Pageable pageable) {
    Page<CurrentTransactionDto> transactions =
        accountService.getTransactionHistory(uuid, pageable);
    return ResponseEntity.ok(ApiResponse.success(transactions));
  }

  @GetMapping("/transactions/{transactionUuid}/verify")
  public ResponseEntity<ApiResponse<TransactionVerification>> verifyByTransactionUuid(
      @PathVariable String transactionUuid) {
    TransactionVerification verification = accountService.verifyByTransactionUuid(transactionUuid);
    return ResponseEntity.ok(ApiResponse.success(verification));
  }
}
