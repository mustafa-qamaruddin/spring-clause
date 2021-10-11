package com.qubits.task.utils.constraints;

import com.qubits.task.utils.validators.DateTimeFutureValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = DateTimeFutureValidator.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface DateTimeFutureConstraint {
  String message() default "Datetime must be greater than now";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
