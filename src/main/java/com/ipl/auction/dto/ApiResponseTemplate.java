package com.ipl.auction.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Standardized API Response Template DTO.
 * Standard contract definition maintained by Member 5 (QA & API Documentation Lead).
 */
@Schema(description = "Standardized REST API Response Wrapper")
public class ApiResponseTemplate<T> {

    @Schema(description = "Timestamp of response generation", example = "2026-08-16T12:00:00")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP Status Code", example = "200")
    private int status;

    @Schema(description = "Response summary message", example = "Operation completed successfully")
    private String message;

    @Schema(description = "Response payload object")
    private T data;

    public ApiResponseTemplate() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponseTemplate(int status, String message, T data) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponseTemplate<T> success(String message, T data) {
        return new ApiResponseTemplate<>(200, message, data);
    }

    public static <T> ApiResponseTemplate<T> error(int status, String message) {
        return new ApiResponseTemplate<>(status, message, null);
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
