package com.mikedevcol.auth_service.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.mikedevcol.auth_service.dto.response.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class SessionExceptionHandler {

  @ExceptionHandler(ActiveSessionViolationException.class)
  public ResponseEntity<ErrorResponse> handleActiveSessionViolationException(ActiveSessionViolationException ex,
      HttpServletRequest request) {

    ErrorResponse errorResponse = ErrorResponse.builder()
        .message(ex.getMessage())
        .details("Active session violation")
        .path(request.getRequestURI())
        .timestamp(LocalDateTime.now().toString())
        .build();

    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

}
