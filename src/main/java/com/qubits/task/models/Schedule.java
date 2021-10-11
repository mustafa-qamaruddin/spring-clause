package com.qubits.task.models;

import lombok.Data;

import java.util.List;

@Data
public class Schedule {
  int month;
  List<MonthPayload> days;
}
