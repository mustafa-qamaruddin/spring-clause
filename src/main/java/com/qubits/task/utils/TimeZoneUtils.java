package com.qubits.task.utils;

import com.qubits.task.exceptions.ThirdPartyErrorException;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static com.qubits.task.configs.Constants.DATETIME_FORMAT;

@Slf4j
public class TimeZoneUtils {
  private final SimpleDateFormat dateTimeFormatter;

  public TimeZoneUtils(String timezone) {
    TimeZone.setDefault(TimeZone.getTimeZone(timezone));
    dateTimeFormatter = new SimpleDateFormat(DATETIME_FORMAT);
  }

  public Date stitchDateParts(int year, int month, int day, String time) {
    if (Objects.isNull(time) || time.isEmpty() || time.isBlank()) {
      var msg = "Invalid time provided: " + time;
      log.error(msg);
      throw new ThirdPartyErrorException(msg, Map.of("time", time));
    }
    Calendar cal = Calendar.getInstance();
    var timeSplits = Arrays.stream(time.split(":")).map(Integer::parseInt).collect(Collectors.toList());
    cal.set(year, month - 1, day, timeSplits.get(0), timeSplits.get(1));
    return cal.getTime();
  }

  public String formatDate(Date inDate) {
    return dateTimeFormatter.format(inDate);
  }
}
