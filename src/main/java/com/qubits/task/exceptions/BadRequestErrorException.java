package com.qubits.task.exceptions;

import java.util.Map;

public class BadRequestErrorException extends RuntimeException {

  Map<String, String> errors;

  public BadRequestErrorException(String message, Map<String, String> searchParams) {
    super(BadRequestErrorException.generateMessage(message, searchParams));
    errors = searchParams;
  }

  private static String generateMessage(String message, Map<String, String> searchParams) {
    return message +
        " for values " +
        searchParams.values();
  }

  public Map<String, String> getErrors() {
    return errors;
  }
}
