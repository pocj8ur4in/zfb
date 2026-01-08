package com.zfb.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * Success response wrapper for API endpoints
 *
 * @param <T> type of the response data
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
  private T data;
  private Meta meta;

  @Getter
  @Builder
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class Meta {
    private LocalDateTime timestamp;

    /**
     * Create meta with timestamp
     *
     * @return Meta with current timestamp
     */
    public static Meta withTimestamp() {
      return Meta.builder().timestamp(LocalDateTime.now()).build();
    }
  }

  /**
   * Create success response with data only
   *
   * @param data data returned by request
   * @param <T> type of data
   * @return success response with data
   */
  public static <T> ApiResponse<T> of(T data) {
    return ApiResponse.<T>builder().data(data).meta(Meta.withTimestamp()).build();
  }
}
