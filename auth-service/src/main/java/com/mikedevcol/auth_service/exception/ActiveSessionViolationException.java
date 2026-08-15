package com.mikedevcol.auth_service.exception;

public class ActiveSessionViolationException extends RuntimeException {

  public ActiveSessionViolationException(String message) {
    super(message);
  }

}
