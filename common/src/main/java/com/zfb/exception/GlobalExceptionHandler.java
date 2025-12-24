package com.zfb.exception;

import com.zfb.dto.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * Handle for business logic exceptions
   *
   * @param e BusinessException
   * @return ResponseEntity with ApiResponse
   */
  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException e) {
    log.warn("Business exception occurred: {}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
  }

  /**
   * Handle for validation exceptions
   *
   * @param e MethodArgumentNotValidException
   * @return ResponseEntity with ApiResponse
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValid(
      MethodArgumentNotValidException e) {
    String message =
        e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
    log.warn("Validation error: {}", message);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(message));
  }

  /**
   * Handle for binding exceptions
   *
   * @param e BindException
   * @return ResponseEntity with ApiResponse
   */
  @ExceptionHandler(BindException.class)
  public ResponseEntity<ApiResponse<Object>> handleBindException(BindException e) {
    String message =
        e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
    log.warn("Bind error: {}", message);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(message));
  }

  /**
   * Handle for constraint violation exceptions
   *
   * @param e ConstraintViolationException
   * @return ResponseEntity with ApiResponse
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(
      ConstraintViolationException e) {
    String message =
        e.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(", "));
    log.warn("Constraint violation: {}", message);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(message));
  }

  /**
   * Handle for HTTP message not readable exceptions
   *
   * @param e HttpMessageNotReadableException
   * @return ResponseEntity with ApiResponse
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadable(
      HttpMessageNotReadableException e) {
    log.warn("HTTP message not readable: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error("Request body is not readable"));
  }

  /**
   * Handle for missing servlet request parameter exceptions
   *
   * @param e MissingServletRequestParameterException
   * @return ResponseEntity with ApiResponse
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ApiResponse<Object>> handleMissingServletRequestParameter(
      MissingServletRequestParameterException e) {
    log.warn("Missing request parameter: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error("Required parameter is missing: " + e.getParameterName()));
  }

  /**
   * Handle for method argument type mismatch exceptions
   *
   * @param e MethodArgumentTypeMismatchException
   * @return ResponseEntity with ApiResponse
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiResponse<Object>> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException e) {
    log.warn("Method argument type mismatch: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error("Parameter type is not valid: " + e.getName()));
  }

  /**
   * Handle for HTTP method not supported exceptions
   *
   * @param e HttpRequestMethodNotSupportedException
   * @return ResponseEntity with ApiResponse
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ApiResponse<Object>> handleHttpRequestMethodNotSupported(
      HttpRequestMethodNotSupportedException e) {
    log.warn("HTTP method not supported: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
        .body(ApiResponse.error("Unsupported HTTP method: " + e.getMethod()));
  }

  /**
   * Handle for no handler found exceptions
   *
   * @param e NoHandlerFoundException
   * @return ResponseEntity with ApiResponse
   */
  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ApiResponse<Object>> handleNoHandlerFoundException(
      NoHandlerFoundException e) {
    log.warn("No handler found: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.error("Requested resource not found"));
  }

  /**
   * Handle for illegal argument exceptions
   *
   * @param e IllegalArgumentException
   * @return ResponseEntity with ApiResponse
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(
      IllegalArgumentException e) {
    log.warn("Illegal argument: {}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
  }

  /**
   * Handle for unexpected exceptions
   *
   * @param e Exception
   * @return ResponseEntity with ApiResponse
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
    log.error("Unexpected error occurred", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error("Internal server error occurred"));
  }
}
