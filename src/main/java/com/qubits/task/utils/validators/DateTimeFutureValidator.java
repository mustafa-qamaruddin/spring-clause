package com.qubits.task.utils.validators;

import com.qubits.task.utils.constraints.DateTimeFutureConstraint;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidationException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.qubits.task.configs.Constants.DATETIME_FORMAT;

@Component
public class DateTimeFutureValidator implements ConstraintValidator<DateTimeFutureConstraint, String> {

  @Override
  public void initialize(DateTimeFutureConstraint constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    // make sure it's a valid date
    Date d = null;
    try {
      d = (new SimpleDateFormat(DATETIME_FORMAT)).parse(value);
    } catch (ParseException e) {
      throw new ValidationException(e);
    }
    // make sure it's a future date
    Date currDate = new Date();
    return (d.compareTo(currDate) > 0);
  }
}
