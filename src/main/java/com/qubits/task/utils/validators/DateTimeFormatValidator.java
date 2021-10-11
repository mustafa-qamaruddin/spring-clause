package com.qubits.task.utils.validators;

import com.qubits.task.utils.constraints.DateTimeFormatConstraint;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static com.qubits.task.configs.Constants.DATETIME_FORMAT;

@Component
public class DateTimeFormatValidator implements ConstraintValidator<DateTimeFormatConstraint, String> {

  @Override
  public void initialize(DateTimeFormatConstraint constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    // make sure it's a valid date
    try {
      (new SimpleDateFormat(DATETIME_FORMAT)).parse(value);
    } catch (ParseException e) {
      return false;
    }
    return true;
  }
}
