package com.qubits.task.models;

import lombok.Data;

@Data
public class Flight {
  String carrierCode, number, departureTime, arrivalTime;
}