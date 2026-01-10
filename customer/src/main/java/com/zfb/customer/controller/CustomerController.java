package com.zfb.customer.controller;

import com.zfb.customer.dto.CustomerDto;
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
}
