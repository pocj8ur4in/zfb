package com.zfb.customer.controller;

import com.zfb.customer.dto.CustomerDto;
import com.zfb.customer.dto.LoginRequest;
import com.zfb.customer.dto.RegisterRequest;
import com.zfb.customer.service.CustomerService;
import com.zfb.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customer", description = "Customer management API")
public class CustomerController {

  private final CustomerService customerService;

  @PostMapping("/register")
  @Operation(summary = "Register new customer", description = "Create a new customer account")
  public ResponseEntity<ApiResponse<CustomerDto>> register(
      @Valid @RequestBody RegisterRequest request) {
    log.info("register request: email={}", request.getEmail());

    return ResponseEntity.ok(ApiResponse.of(customerService.register(request)));
  }

  @GetMapping("/{uuid}")
  @Operation(
      summary = "Get customer by uuid",
      description = "Retrieve customer information by uuid")
  public ResponseEntity<ApiResponse<CustomerDto>> getCustomer(@PathVariable String uuid) {
    log.info("get customer: uuid={}", uuid);

    return ResponseEntity.ok(ApiResponse.of(customerService.getCustomerByUuid(uuid)));
  }

  @GetMapping("/email/{email}")
  @Operation(
      summary = "Get customer by email",
      description = "Retrieve customer information by email")
  public ResponseEntity<ApiResponse<CustomerDto>> getCustomerByEmail(@PathVariable String email) {
    log.info("get customer by email: email={}", email);

    return ResponseEntity.ok(ApiResponse.of(customerService.getCustomerByEmail(email)));
  }

  @PostMapping("/validate")
  @Operation(
      summary = "Validate customer password",
      description = "Validate customer email and password for authentication")
  public ResponseEntity<ApiResponse<Boolean>> validatePassword(
      @Valid @RequestBody LoginRequest request) {
    log.info("validate password: email={}", request.getEmail());

    return ResponseEntity.ok(
        ApiResponse.of(
            customerService.validatePassword(request.getEmail(), request.getPassword())));
  }
}
