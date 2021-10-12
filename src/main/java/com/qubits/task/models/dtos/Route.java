package com.qubits.task.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Route {
  private String airportFrom, airportTo, operator, carrierCode, group, connectingAirport;
  private boolean newRoute, seasonalRoute;
  private List<String> similarArrivalAirportCodes;
  private List<String> tags;
}
