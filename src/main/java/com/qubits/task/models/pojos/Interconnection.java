package com.qubits.task.models.pojos;

import lombok.Data;

import java.util.List;

@Data
public class Interconnection {
  int stops;
  List<Leg> legs;
}
