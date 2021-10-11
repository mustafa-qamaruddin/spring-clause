package com.qubits.task.models.pojos;

import lombok.Data;

@Data
public class Leg {
  private String departureAirport, arrivalAirport, departureDateTime, arrivalDateTime;
}
