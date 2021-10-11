package com.qubits.task.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Flight {
  @JsonProperty("carrierCode")
  private String carrierCode;
  @JsonProperty("number")
  private String number;
  @JsonProperty("departureTime")
  private String departureTime;
  @JsonProperty("arrivalTime")
  private String arrivalTime;
}