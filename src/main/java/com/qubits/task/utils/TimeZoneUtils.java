package com.qubits.task.utils;

import java.util.TimeZone;

public class TimeZoneUtils {
  public TimeZoneUtils(String timezone) {
    TimeZone.setDefault(TimeZone.getTimeZone(timezone));
  }
}
