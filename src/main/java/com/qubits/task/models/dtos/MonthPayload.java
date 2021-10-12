package com.qubits.task.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonthPayload {
  @JsonProperty("day")
  int day;
  @JsonProperty("flights")
  List<Flight> flights;
}
