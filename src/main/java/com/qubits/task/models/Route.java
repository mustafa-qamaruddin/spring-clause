package com.qubits.task.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Route {
  private String airportFrom, airportTo, operator, carrierCode, group, connectingAirport;
  private boolean newRoute, seasonalRoute;
  private List<String> similarArrivalAirportCodes;
  private List<String> tags;
}
