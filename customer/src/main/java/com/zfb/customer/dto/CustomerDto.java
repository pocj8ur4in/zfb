package com.zfb.customer.dto;

import com.zfb.customer.domain.Customer;
import com.zfb.customer.domain.CustomerStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {
  private Long id;
  private String email;
  private String username;
  private String name;
  private String phoneNumber;
  private LocalDate dateOfBirth;
  private CustomerStatus status;
  private LocalDateTime createdAt;

  public static CustomerDto from(Customer customer) {
    return new CustomerDto(
        customer.getId(),
        customer.getEmail(),
        customer.getUsername(),
        customer.getName(),
        customer.getPhoneNumber(),
        customer.getDateOfBirth(),
        customer.getStatus(),
        customer.getCreatedAt());
  }
}
