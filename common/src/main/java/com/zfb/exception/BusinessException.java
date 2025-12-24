package com.zfb.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

  private final String code;

  /**
   * Constructor for BusinessException with message
   *
   * @param message message describing result of request
   */
  public BusinessException(String message) {
    super(message);
    this.code = BusinessErrorCode.BUSINESS_ERROR.getCode();
  }

  /**
   * Constructor for BusinessException with code, message
   *
   * @param code error code
   * @param message message describing result of request
   */
  public BusinessException(String code, String message) {
    super(message);
    this.code = code;
  }

  /**
   * Constructor for BusinessException with message, cause
   *
   * @param message message describing result of request
   * @param cause cause of exception
   */
  public BusinessException(String message, Throwable cause) {
    super(message, cause);
    this.code = BusinessErrorCode.BUSINESS_ERROR.getCode();
  }

  /**
   * Constructor for BusinessException with code, message, cause
   *
   * @param code error code
   * @param message message describing result of request
   * @param cause cause of exception
   */
  public BusinessException(String code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
  }
}
