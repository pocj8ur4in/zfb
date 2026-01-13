package com.zfb.customer.dto;

import com.zfb.customer.domain.Customer;
import com.zfb.customer.domain.CustomerStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
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
    CustomerDto dto = new CustomerDto();
    dto.id = customer.getId();
    dto.email = customer.getEmail();
    dto.username = customer.getUsername();
    dto.name = customer.getName();
    dto.phoneNumber = customer.getPhoneNumber();
    dto.dateOfBirth = customer.getDateOfBirth();
    dto.status = customer.getStatus();
    dto.createdAt = customer.getCreatedAt();
    return dto;
  }
}
