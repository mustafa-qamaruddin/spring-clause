package com.qubits.task.utils;

import com.qubits.task.exceptions.BadRequestErrorException;

import javax.validation.ValidationException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.qubits.task.configs.Constants.DATETIME_FORMAT;

public class SearchFormUtils {

  private final SimpleDateFormat dateTimeFormatter;
  private final TimeZoneUtils timeZoneUtils;

  public SearchFormUtils(TimeZoneUtils timeZoneUtils) {
    this.timeZoneUtils = timeZoneUtils;
    dateTimeFormatter = new SimpleDateFormat(DATETIME_FORMAT);
  }

  public Map<String, Date> parseDateTimes(Map<String, String> inputs) {
    Map<String, Date> dates = new HashMap<>();
    inputs.forEach((k, v) -> {
      try {
        dates.put(k, dateTimeFormatter.parse(v));
      } catch (ParseException e) {
        throw new ValidationException(e);
      }
    });
    return dates;
  }

  public void isDepartureEarlierThanArrival(Date departure, Date arrival) {
    if (departure.compareTo(arrival) > 0) {
      throw new BadRequestErrorException("Time travel is suspended until further notice. Departure must always be " +
          "earlier than arrival", Map.of("departure", departure.toString(), "arrival", arrival.toString()));
    }
  }
}
