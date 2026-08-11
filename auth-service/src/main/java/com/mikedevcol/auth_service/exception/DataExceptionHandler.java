package com.mikedevcol.auth_service.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.mikedevcol.auth_service.dto.response.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class DataExceptionHandler {

  @ExceptionHandler(DataNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleDataNotFoundException(
      DataNotFoundException ex,
      HttpServletRequest request) {
    ErrorResponse errorResponse = ErrorResponse.builder()
        .message(ex.getMessage())
        .details("Data not found")
        .path(request.getRequestURI())
        .timestamp(String.valueOf(System.currentTimeMillis()))
        .build();
    return ResponseEntity.status(404).body(errorResponse);
  }

  @ExceptionHandler(DataConflictException.class)
  public ResponseEntity<ErrorResponse> handleDataConflictException(
      DataConflictException ex,
      HttpServletRequest request) {
    ErrorResponse errorResponse = ErrorResponse.builder()
        .message(ex.getMessage())
        .details("Data conflict")
        .path(request.getRequestURI())
        .timestamp(String.valueOf(System.currentTimeMillis()))
        .build();
    return ResponseEntity.status(409).body(errorResponse);
  }

}
