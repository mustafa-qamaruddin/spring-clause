package com.qubits.task.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Schedule {
  @JsonProperty("month")
  int month;
  @JsonProperty("days")
  List<MonthPayload> days;
  @JsonIgnore
  int year;

  public Schedule setYear(int y) {
    this.year = y;
    return this;
  }
}
