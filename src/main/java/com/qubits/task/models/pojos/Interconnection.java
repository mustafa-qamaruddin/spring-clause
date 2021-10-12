package com.qubits.task.models.pojos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Interconnection {
  int stops;
  List<Leg> legs;

  @JsonIgnore
  boolean isTransit;
  @JsonIgnore
  boolean isVisited;

  public Interconnection() {
  }

  public Interconnection(Interconnection interconnection) {
    this.stops = interconnection.stops;
    this.isTransit = interconnection.isTransit;
    this.legs = new ArrayList<>();
    this.legs.addAll(interconnection.getLegs());
  }

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

  public Interconnection setTransit(boolean isTransit) {
    this.isTransit = true;
    return this;
  }

  public Interconnection setVisited(boolean isVisited) {
    this.isVisited = true;
    return this;
  }
}
