package com.qubits.task.models;

import lombok.Data;

import java.util.List;

@Data
public class MonthPayload {
  int day;
  List<Flight> flights;
}
