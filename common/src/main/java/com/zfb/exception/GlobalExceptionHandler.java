package com.zfb.exception;

import com.zfb.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
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
   * @param request HttpServletRequest
   * @return ResponseEntity with ErrorResponse
   */
  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(
      BusinessException e, HttpServletRequest request) {
    log.warn("Business exception occurred: {}", e.getMessage(), e);

    ErrorResponse errorResponse =
        ErrorResponse.of(HttpStatus.BAD_REQUEST, e.getCode(), e.getMessage()).toBuilder()
            .instance(request.getRequestURI())
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(errorResponse);
  }

  /**
   * Handle for validation exceptions
   *
   * @param e MethodArgumentNotValidException
   * @param request HttpServletRequest
   * @return ResponseEntity with ErrorResponse
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
      MethodArgumentNotValidException e, HttpServletRequest request) {
    Map<String, Object> errors = new HashMap<>();
    e.getBindingResult()
        .getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

    String detail = "Validation failed for " + errors.size() + " field(s)";
    log.warn("Validation error: {}", errors);

    ErrorResponse errorResponse =
        ErrorResponse.of(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", detail, errors).toBuilder()
            .instance(request.getRequestURI())
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(errorResponse);
  }

  /**
   * Handle for binding exceptions
   *
   * @param e BindException
   * @param request HttpServletRequest
   * @return ResponseEntity with ErrorResponse
   */
  @ExceptionHandler(BindException.class)
  public ResponseEntity<ErrorResponse> handleBindException(
      BindException e, HttpServletRequest request) {
    Map<String, Object> errors = new HashMap<>();
    e.getBindingResult()
        .getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

    String detail = "Binding failed for " + errors.size() + " field(s)";
    log.warn("Bind error: {}", errors);

    ErrorResponse errorResponse =
        ErrorResponse.of(HttpStatus.BAD_REQUEST, "BIND_ERROR", detail, errors).toBuilder()
            .instance(request.getRequestURI())
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(errorResponse);
  }

  /**
   * Handle for constraint violation exceptions
   *
   * @param e ConstraintViolationException
   * @param request HttpServletRequest
   * @return ResponseEntity with ErrorResponse
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(
      ConstraintViolationException e, HttpServletRequest request) {
    Map<String, Object> violations = new HashMap<>();
    e.getConstraintViolations()
        .forEach(
            violation ->
                violations.put(violation.getPropertyPath().toString(), violation.getMessage()));

    String detail = "Constraint violation for " + violations.size() + " field(s)";
    log.warn("Constraint violation: {}", violations);

    ErrorResponse errorResponse =
        ErrorResponse.of(HttpStatus.BAD_REQUEST, "CONSTRAINT_VIOLATION", detail, violations)
            .toBuilder()
            .instance(request.getRequestURI())
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(errorResponse);
  }

  /**
   * Handle for HTTP message not readable exceptions
   *
   * @param e HttpMessageNotReadableException
   * @param request HttpServletRequest
   * @return ResponseEntity with ErrorResponse
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
      HttpMessageNotReadableException e, HttpServletRequest request) {
    log.warn("HTTP message not readable: {}", e.getMessage());

    ErrorResponse errorResponse =
        ErrorResponse.of(
                HttpStatus.BAD_REQUEST,
                "MESSAGE_NOT_READABLE",
                "Request body is not readable or malformed")
            .toBuilder()
            .instance(request.getRequestURI())
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(errorResponse);
  }

  /**
   * Handle for missing servlet request parameter exceptions
   *
   * @param e MissingServletRequestParameterException
   * @param request HttpServletRequest
   * @return ResponseEntity with ErrorResponse
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(
      MissingServletRequestParameterException e, HttpServletRequest request) {
    log.warn("Missing request parameter: {}", e.getMessage());

    String detail =
        String.format(
            "Required parameter '%s' of type '%s' is missing",
            e.getParameterName(), e.getParameterType());

    ErrorResponse errorResponse =
        ErrorResponse.of(HttpStatus.BAD_REQUEST, "MISSING_PARAMETER", detail).toBuilder()
            .instance(request.getRequestURI())
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(errorResponse);
  }

  /**
   * Handle for method argument type mismatch exceptions
   *
   * @param e MethodArgumentTypeMismatchException
   * @param request HttpServletRequest
   * @return ResponseEntity with ErrorResponse
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException e, HttpServletRequest request) {
    log.warn("Method argument type mismatch: {}", e.getMessage());

    String detail =
        String.format(
            "Parameter '%s' should be of type '%s'",
            e.getName(),
            e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown");

    ErrorResponse errorResponse =
        ErrorResponse.of(HttpStatus.BAD_REQUEST, "TYPE_MISMATCH", detail).toBuilder()
            .instance(request.getRequestURI())
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(errorResponse);
  }

  /**
   * Handle for HTTP method not supported exceptions
   *
   * @param e HttpRequestMethodNotSupportedException
   * @param request HttpServletRequest
   * @return ResponseEntity with ErrorResponse
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(
      HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
    log.warn("HTTP method not supported: {}", e.getMessage());

    String detail =
        String.format(
            "Method '%s' is not supported for this endpoint. Supported methods: %s",
            e.getMethod(), e.getSupportedHttpMethods());

    ErrorResponse errorResponse =
        ErrorResponse.of(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", detail).toBuilder()
            .instance(request.getRequestURI())
            .build();

    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(errorResponse);
  }

  /**
   * Handle for no handler found exceptions
   *
   * @param e NoHandlerFoundException
   * @param request HttpServletRequest
   * @return ResponseEntity with ErrorResponse
   */
  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(
      NoHandlerFoundException e, HttpServletRequest request) {
    log.warn("No handler found: {}", e.getMessage());

    String detail =
        String.format("No endpoint found for '%s %s'", e.getHttpMethod(), e.getRequestURL());

    ErrorResponse errorResponse =
        ErrorResponse.of(HttpStatus.NOT_FOUND, "NOT_FOUND", detail).toBuilder()
            .instance(request.getRequestURI())
            .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(errorResponse);
  }

  /**
   * Handle for distributed lock acquisition failures
   *
   * @param e LockAcquisitionException
   * @param request HttpServletRequest
   * @return ResponseEntity with ErrorResponse
   */
  @ExceptionHandler(com.zfb.lock.LockAcquisitionException.class)
  public ResponseEntity<ErrorResponse> handleLockAcquisitionException(
      com.zfb.lock.LockAcquisitionException e, HttpServletRequest request) {
    log.warn("Lock acquisition failed: {}", e.getMessage());

    ErrorResponse errorResponse =
        ErrorResponse.of(
                HttpStatus.SERVICE_UNAVAILABLE,
                "LOCK_ACQUISITION_FAILED",
                "Resource is temporarily unavailable due to concurrent access. Please retry.")
            .toBuilder()
            .instance(request.getRequestURI())
            .build();

    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(errorResponse);
  }

  /**
   * Handle for illegal argument exceptions
   *
   * @param e IllegalArgumentException
   * @param request HttpServletRequest
   * @return ResponseEntity with ErrorResponse
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException e, HttpServletRequest request) {
    log.warn("Illegal argument: {}", e.getMessage(), e);

    ErrorResponse errorResponse =
        ErrorResponse.of(HttpStatus.BAD_REQUEST, "ILLEGAL_ARGUMENT", e.getMessage()).toBuilder()
            .instance(request.getRequestURI())
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(errorResponse);
  }

  /**
   * Handle for service unavailable exceptions
   *
   * @param e ServiceUnavailableException
   * @param request HttpServletRequest
   * @return ResponseEntity with ErrorResponse
   */
  @ExceptionHandler(ServiceUnavailableException.class)
  public ResponseEntity<ErrorResponse> handleServiceUnavailable(
      ServiceUnavailableException e, HttpServletRequest request) {
    log.warn("Service unavailable: {}", e.getMessage(), e);

    ErrorResponse errorResponse =
        ErrorResponse.of(HttpStatus.SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", e.getMessage())
            .toBuilder()
            .instance(request.getRequestURI())
            .build();

    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(errorResponse);
  }

  /**
   * Handle for unexpected exceptions
   *
   * @param e Exception
   * @param request HttpServletRequest
   * @return ResponseEntity with ErrorResponse
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
    log.error("Unexpected error occurred", e);

    ErrorResponse errorResponse =
        ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred. Please contact support if the problem persists.")
            .toBuilder()
            .instance(request.getRequestURI())
            .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(errorResponse);
  }
}
