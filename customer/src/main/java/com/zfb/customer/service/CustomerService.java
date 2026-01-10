package com.zfb.customer.service;

import com.zfb.customer.domain.Customer;
import com.zfb.customer.dto.CustomerDto;
import com.zfb.customer.dto.RegisterRequest;
import com.zfb.customer.repository.CustomerRepository;
import com.zfb.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

  private final CustomerRepository customerRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public CustomerDto register(RegisterRequest request) {
    log.info("registering new customer: {}", request.getEmail());

    if (customerRepository.existsByEmail(request.getEmail())) {
      throw new BusinessException("email already exists");
    }

    if (customerRepository.existsByUsername(request.getUsername())) {
      throw new BusinessException("username already exists");
    }

    String encodedPassword = passwordEncoder.encode(request.getPassword());

    Customer customer =
        Customer.builder()
            .email(request.getEmail())
            .username(request.getUsername())
            .password(encodedPassword)
            .name(request.getName())
            .phoneNumber(request.getPhoneNumber())
            .dateOfBirth(request.getDateOfBirth())
            .build();

    Customer saved = customerRepository.save(customer);
    log.info("customer registered successfully: id={}, email={}", saved.getId(), saved.getEmail());

    return CustomerDto.from(saved);
  }
}
