package com.qubits.task.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MonthPayload {
  @JsonProperty("day")
  int day;
  @JsonProperty("flights")
  List<Flight> flights;
}
