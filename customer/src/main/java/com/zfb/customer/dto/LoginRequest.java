package com.zfb.customer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {

  @NotBlank(message = "email is required")
  private String email;

  @NotBlank(message = "password is required")
  private String password;
}
