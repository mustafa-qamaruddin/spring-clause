package com.qubits.task.models;

import lombok.Data;

import java.util.List;

@Data
public class Route {
  private String airportFrom, airportTo, operator, carrierCode, group, connectingAirport;
  private boolean newRoute, seasonalRoute;
  private List<String> similarArrivalAirportCodes;
  private List<String> tags;
}
