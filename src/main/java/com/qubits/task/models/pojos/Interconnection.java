package com.qubits.task.models.pojos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Interconnection {
  int stops;
  List<Leg> legs;

  public Interconnection incrementStops() {
    stops++;
    return this;
  }

  public Interconnection addLeg(Leg leg) {
    if (legs == null) {
      legs = new ArrayList<>();
    }
    legs.add(leg);
    return this;
  }
}
