package com.zfb.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterRequest {

  @NotBlank(message = "email is required")
  @Email(message = "invalid email format")
  private String email;

  @NotBlank(message = "username is required")
  @Size(min = 3, max = 50, message = "username must be between 3 and 50 characters")
  @Pattern(
      regexp = "^[a-zA-Z0-9_]+$",
      message = "username can only contain letters, numbers, and underscores")
  private String username;

  @NotBlank(message = "password is required")
  @Size(min = 8, max = 100, message = "password must be between 8 and 100 characters")
  private String password;

  @NotBlank(message = "name is required")
  @Size(max = 100, message = "name must be less than 100 characters")
  private String name;

  @Pattern(regexp = "^[0-9]{10,11}$", message = "invalid phone number format")
  private String phoneNumber;

  private LocalDate dateOfBirth;
}
