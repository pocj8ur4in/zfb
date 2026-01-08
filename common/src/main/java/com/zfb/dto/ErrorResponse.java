package com.zfb.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/** Error response wrapper for API endpoints */
@Getter
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
  private String type;
  private String title;
  private Integer status;
  private String detail;
  private String instance;
  private Map<String, Object> extensions;
  private LocalDateTime timestamp;

  /**
   * Create error response
   *
   * @param status HTTP status
   * @param detail error detail message
   * @return ErrorResponse
   */
  public static ErrorResponse of(HttpStatus status, String detail) {
    return ErrorResponse.builder()
        .type("about:blank")
        .title(status.name())
        .status(status.value())
        .detail(detail)
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * Create error response with error code
   *
   * @param status HTTP status
   * @param errorCode error code
   * @param detail error detail message
   * @return ErrorResponse
   */
  public static ErrorResponse of(HttpStatus status, String errorCode, String detail) {
    return ErrorResponse.builder()
        .type("about:blank")
        .title(errorCode)
        .status(status.value())
        .detail(detail)
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * Create error response with error code and extensions
   *
   * @param status HTTP status
   * @param errorCode error code
   * @param detail error detail message
   * @param extensions additional error properties
   * @return ErrorResponse
   */
  public static ErrorResponse of(
      HttpStatus status, String errorCode, String detail, Map<String, Object> extensions) {
    return ErrorResponse.builder()
        .type("about:blank")
        .title(errorCode)
        .status(status.value())
        .detail(detail)
        .extensions(extensions)
        .timestamp(LocalDateTime.now())
        .build();
  }
}
