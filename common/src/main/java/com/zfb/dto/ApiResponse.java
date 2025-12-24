package com.zfb.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
  private boolean isSuccess;
  private String message;
  private T data;
  private LocalDateTime timestamp;

  public ApiResponse(boolean isSuccess, String message, T data, LocalDateTime timestamp) {
    this.isSuccess = isSuccess;
    this.message = message;
    this.data = data;
    this.timestamp = timestamp;
  }

  /**
   * Return success response with data
   *
   * @param data data returned by request
   * @return success response with data
   */
  public static <T> ApiResponse<T> success(T data) {
    return ApiResponse.<T>builder()
        .isSuccess(true)
        .data(data)
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * Return success response with message and data
   *
   * @param message message describing result of request
   * @param data data returned by request
   * @return success response with message and data
   */
  public static <T> ApiResponse<T> success(String message, T data) {
    return ApiResponse.<T>builder()
        .isSuccess(true)
        .message(message)
        .data(data)
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * Return success response with message
   *
   * @param message message describing result of request
   * @return success response with message
   */
  public static <T> ApiResponse<T> success(String message) {
    return ApiResponse.<T>builder()
        .isSuccess(true)
        .message(message)
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * Return error response with message
   *
   * @param message message describing result of request
   * @return error response with message
   */
  public static <T> ApiResponse<T> error(String message) {
    return ApiResponse.<T>builder()
        .isSuccess(false)
        .message(message)
        .timestamp(LocalDateTime.now())
        .build();
  }
}
