package com.qubits.task.services;

import com.qubits.task.models.CustomDate;
import com.qubits.task.models.Interconnection;
import com.qubits.task.models.Route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.qubits.task.configs.Constants.RYAN_AIR;

public class FlightFinderService {
  private final AirlineClient airlineClient;

  public FlightFinderService(AirlineClient airlineClient) {
    this.airlineClient = airlineClient;
  }

  public List<Interconnection> findInterconnections(
      String fromAirport,
      String toAirport,
      Date departure,
      Date arrival
  ) {
    var possibleRoutes = getPossibleRoutes(fromAirport, toAirport);
    var departureDate = new CustomDate(departure);
    var arrivalDate = new CustomDate(arrival);
    return new ArrayList<>();
  }

  public List<Route> getPossibleRoutes(String airportFrom, String to) {
    // Please note that only routes with:
    // connectingAirport set to null and operator set to RYANAIR should be used
    return Optional.of(airlineClient.fetchRoutes(airportFrom))
        .orElse(Collections.emptyList())
        .stream()
        .filter(r -> Objects.equals(r.getOperator(), RYAN_AIR) && Objects.equals(null, r.getConnectingAirport()) &&
            Objects.equals(r.getAirportTo(), to))
        .collect(Collectors.toList());
  }
}
