package com.zfb.exception;

import lombok.Getter;

@Getter
public enum BusinessErrorCode {
  BUSINESS_ERROR("BUSINESS_ERROR");

  private final String message;

  BusinessErrorCode(String message) {
    this.message = message;
  }
}
