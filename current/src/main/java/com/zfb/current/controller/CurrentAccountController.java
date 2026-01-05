package com.zfb.current.controller;

import com.zfb.current.dto.*;
import com.zfb.current.service.CurrentAccountService;
import com.zfb.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
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
  @Operation(
      summary = "Create Current Account",
      description = "create a new current account with given user uuid.")
  public ResponseEntity<ApiResponse<CurrentAccountDto>> createAccount(
      @Valid @RequestBody CreateAccountRequest request) {
    CurrentAccountDto account = accountService.createAccount(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success("account created successfully", account));
  }

  @GetMapping("/{accountUuid}")
  @Operation(
      summary = "Get Current Account",
      description = "get a current account by account uuid.")
  public ResponseEntity<ApiResponse<CurrentAccountDto>> getAccount(
      @PathVariable String accountUuid) {
    CurrentAccountDto account = accountService.getAccount(accountUuid);
    return ResponseEntity.ok(ApiResponse.success(account));
  }

  @GetMapping("/user/{userUuid}")
  @Operation(
      summary = "Get Current Accounts",
      description = "get a list of current accounts by user uuid.")
  public ResponseEntity<ApiResponse<List<CurrentAccountDto>>> getAccountsByUserUuid(
      @PathVariable String userUuid) {
    List<CurrentAccountDto> accounts = accountService.getAccountsByUserUuid(userUuid);
    return ResponseEntity.ok(ApiResponse.success(accounts));
  }

  @PostMapping("/{accountUuid}/withdraw")
  @Operation(
      summary = "Withdraw from Current Account",
      description = "withdraw from a current account by account uuid.")
  public ResponseEntity<ApiResponse<CurrentTransactionDto>> withdraw(
      @PathVariable String accountUuid, @Valid @RequestBody WithdrawRequest request) {
    CurrentTransactionDto transaction = accountService.withdraw(accountUuid, request);
    return ResponseEntity.ok(ApiResponse.success("withdraw completed successfully", transaction));
  }

  @PostMapping("/{accountUuid}/deposit")
  @Operation(
      summary = "Deposit to Current Account",
      description = "deposit to a current account by account uuid.")
  public ResponseEntity<ApiResponse<CurrentTransactionDto>> deposit(
      @PathVariable String accountUuid, @Valid @RequestBody DepositRequest request) {
    CurrentTransactionDto transaction = accountService.deposit(accountUuid, request);
    return ResponseEntity.ok(ApiResponse.success("deposit completed successfully", transaction));
  }

  @PostMapping("/transactions/{transactionUuid}/refund")
  @Operation(
      summary = "Refund Transaction",
      description = "refund a transaction by transaction uuid.")
  public ResponseEntity<ApiResponse<CurrentTransactionDto>> refund(
      @PathVariable String transactionUuid, @RequestParam String reason) {
    CurrentTransactionDto transaction = accountService.refund(transactionUuid, reason);
    return ResponseEntity.ok(ApiResponse.success("refund completed", transaction));
  }

  @GetMapping("/{accountUuid}/transactions")
  @Operation(
      summary = "Get Transaction History",
      description = "get a list of transactions by account uuid.")
  public ResponseEntity<ApiResponse<Page<CurrentTransactionDto>>> getTransactionHistory(
      @PathVariable String accountUuid, Pageable pageable) {
    Page<CurrentTransactionDto> transactions =
        accountService.getTransactionHistory(accountUuid, pageable);
    return ResponseEntity.ok(ApiResponse.success(transactions));
  }

  @GetMapping("/transactions/{transactionUuid}/verify")
  @Operation(
      summary = "Verify Transaction",
      description = "verify a transaction by transaction uuid.")
  public ResponseEntity<ApiResponse<TransactionVerification>> verifyByTransactionUuid(
      @PathVariable String transactionUuid) {
    TransactionVerification verification = accountService.verifyByTransactionUuid(transactionUuid);
    return ResponseEntity.ok(ApiResponse.success(verification));
  }

  @GetMapping("/transactions/verify")
  @Operation(
      summary = "Verify Transaction",
      description = "verify a transaction by client request uuid.")
  public ResponseEntity<ApiResponse<TransactionVerification>> verifyByClientRequestUuid(
      @RequestParam String clientRequestUuid) {
    TransactionVerification verification =
        accountService.verifyByClientRequestUuid(clientRequestUuid);
    return ResponseEntity.ok(ApiResponse.success(verification));
  }
}
