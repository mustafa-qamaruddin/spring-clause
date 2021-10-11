package com.qubits.task.exceptions;

import org.springframework.util.StringUtils;

import java.util.Map;

public class ThirdPartyErrorException extends RuntimeException {

  Map<String, String> errors;

  public ThirdPartyErrorException(String message, Map<String, String> searchParams) {
    super(ThirdPartyErrorException.generateMessage(message, searchParams));
    errors = searchParams;
  }

  private static String generateMessage(String message, Map<String, String> searchParams) {
    return message +
        " for parameters " +
        searchParams.values();
  }

  public Map<String, String> getErrors() {
    return errors;
  }
}
