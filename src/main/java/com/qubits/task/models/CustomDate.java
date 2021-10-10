package com.qubits.task.models;

import java.util.Date;
import java.util.TimeZone;

public class CustomDate {
  private Date rawDate;

  public CustomDate(Date rawDate) {
    this.rawDate = rawDate;
  }

  public Date getD() {
    return rawDate;
  }

  public int getMinute() {
    return rawDate.toInstant().atZone(TimeZone.getDefault().toZoneId()).getMinute();
  }

  public int getHour() {
    return rawDate.toInstant().atZone(TimeZone.getDefault().toZoneId()).getHour();
  }

  public int getDay() {
    return rawDate.toInstant().atZone(TimeZone.getDefault().toZoneId()).getDayOfMonth();
  }

  public int getMonth() {
    return rawDate.toInstant().atZone(TimeZone.getDefault().toZoneId()).getMonth().getValue();
  }

  public int getYear() {
    return rawDate.toInstant().atZone(TimeZone.getDefault().toZoneId()).getYear();
  }
}
