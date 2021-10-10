package com.qubits.task.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ValidationUtils {

  private final SimpleDateFormat dateTimeFormatter;

  public ValidationUtils(String timezone) {
    dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    TimeZone.setDefault(TimeZone.getTimeZone(timezone));
  }

  public Date validateDateTime(String input) {
    // make sure it's a valid date
    Date d = null;
    try {
      d = dateTimeFormatter.parse(input);
    } catch (ParseException e) {
      throw new IllegalArgumentException("Invalid datetime value found when parsing: " + input);
    }
    // make sure it's a future date
    Date currDate = new Date();
    if (d.compareTo(currDate) < 0) {
      throw new IllegalArgumentException("Datetime must be greater than now: " + input);
    } else {
      return d;
    }
  }
}
