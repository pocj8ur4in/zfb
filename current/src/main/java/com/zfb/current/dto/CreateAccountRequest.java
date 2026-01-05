package com.zfb.current.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateAccountRequest {

  @NotNull(message = "user uuid is required")
  private String userUuid;
}
