package com.qubits.task.models.pojos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;

@Data
public class Leg {
  private String departureAirport, arrivalAirport, departureDateTime, arrivalDateTime;

  @JsonIgnore
  Date arrivalDateTimeRaw;
}
