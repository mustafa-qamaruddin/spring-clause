package com.qubits.task.utils;

import java.util.Date;
import java.util.TimeZone;

import static java.lang.Math.abs;

public class CustomDate {
  private final Date rawDate;

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

  public Date getRawDate() {
    return rawDate;
  }

  public int getMonth() {
    return rawDate.toInstant().atZone(TimeZone.getDefault().toZoneId()).getMonth().getValue();
  }

  public int getYear() {
    return rawDate.toInstant().atZone(TimeZone.getDefault().toZoneId()).getYear();
  }

  // assume that d is always after this
  public int[] yearDiff(CustomDate d) {
    int diff = abs(d.getYear() - this.getYear()) + 1;
    int[] ret = new int[diff];
    var start = d.getYear();
    if (this.getYear() < d.getYear())
      start = d.getYear();
    int j = 0;
    for (int i = start; i < start + diff; i++) {
      ret[j] = i;
      j++;
    }
    return ret;
  }

  public int[] monthsDiff(int untilMonth) {
    int numMonths = untilMonth - this.getMonth() + 1;
    int[] months = new int[numMonths];
    int j = 0;
    for (int i = this.getMonth(); i < this.getMonth() + numMonths; i++) {
      months[j] = i;
      j++;
    }
    return months;
  }

  public boolean isEarlierThanOrEqual(CustomDate d) {
    return (this.getRawDate().compareTo(d.getRawDate()) <= 0);
  }
}
