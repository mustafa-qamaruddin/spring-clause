package com.qubits.task.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Schedule {
  @JsonProperty("month")
  int month;
  @JsonProperty("days")
  List<MonthPayload> days;
}
