package com.zfb.exception;

import lombok.Getter;

@Getter
public enum BusinessErrorCode {
  BUSINESS_ERROR("BUSINESS_ERROR"),

  RESOURCE_LOCK_ACQUISITION_FAILED("RESOURCE_LOCK_ACQUISITION_FAILED"),
  RESOURCE_IS_LOCKED("RESOURCE_IS_LOCKED");

  private final String code;

  BusinessErrorCode(String code) {
    this.code = code;
  }
}
