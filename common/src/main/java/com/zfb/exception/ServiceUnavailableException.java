package com.zfb.exception;

/** Exception thrown when a service is unavailable */
public class ServiceUnavailableException extends RuntimeException {

  /**
   * Constructor with message and cause
   *
   * @param message error message
   * @param cause original exception
   */
  public ServiceUnavailableException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor with message
   *
   * @param message error message
   */
  public ServiceUnavailableException(String message) {
    super(message);
  }
}
