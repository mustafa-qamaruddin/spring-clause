package com.qubits.task.exceptions;

import org.springframework.util.StringUtils;

public class EntityNotFoundException extends RuntimeException {
  public EntityNotFoundException(Class clazz, String... searchParams) {
    super(EntityNotFoundException.generateMessage(clazz.getSimpleName(), searchParams));
  }

  private static String generateMessage(String entity, String[] searchParams) {
    return StringUtils.capitalize(entity) +
        " was not found for parameters " +
        searchParams;
  }

}
