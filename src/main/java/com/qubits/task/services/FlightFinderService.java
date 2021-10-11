package com.qubits.task.services;

import com.qubits.task.exceptions.ThirdPartyErrorException;
import com.qubits.task.models.CustomDate;
import com.qubits.task.models.Interconnection;
import com.qubits.task.models.Route;
import com.qubits.task.models.Schedule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.qubits.task.configs.Constants.RYAN_AIR;

@Slf4j
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
    Map<Integer, List<Integer>> yearMonthPairs = getPairs(departureDate, arrivalDate);
    Map<Route, List<Schedule>> routesScheds = new HashMap<>();
    possibleRoutes.forEach(r -> routesScheds.put(r, getSchedules(r.getAirportFrom(), r.getAirportTo(), yearMonthPairs)));
    return mapScheduledToInterconnections(routesScheds);
  }

  private List<Interconnection> mapScheduledToInterconnections(Map<Route, List<Schedule>> routesScheds) {
    List<Interconnection> interconnections = new ArrayList<>();

    return interconnections;
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

  public List<Schedule> getSchedules(String airportFrom, String airportTo, Map<Integer, List<Integer>> yearMonthPairs) {
    var response = new ArrayList<Schedule>();
    var params = new HashMap<>(Map.of("airportFrom", airportFrom, "airportTo", airportTo));
    yearMonthPairs.forEach((year, months) -> response.addAll(airlineClient.fetchSchedules(airportFrom, airportTo, year,
        months, params)));
    // For interconnected flights the difference between the arrival and the next departure
    // should be 2h or greater
    response.stream().filter()
    return response;
  }

  public Map<Integer, List<Integer>> getPairs(CustomDate start, CustomDate end) {
    // @todo make sure start is always before end
    // @todo validation done early in form utils
    if (!start.isEarlierThanOrEqual(end)) {
      var errMsg = "Validation bypassed, departure cannot be after arrival date" + start.toString() +
          " -> " + end.toString();
      log.error(errMsg);
      throw new RuntimeException(errMsg);
    }
    Map<Integer, List<Integer>> ret = new HashMap<>();
    int[] yearsBetween = start.yearDiff(end);
    boolean spansManyYears = yearsBetween.length > 1;
    Arrays.stream(yearsBetween).forEach(y -> {
      List<Integer> months = new ArrayList<>();
      boolean isFirstYear = (y == start.getYear());
      boolean isLastYear = (y == end.getYear());
      if (!spansManyYears) {
        months.addAll(Arrays.asList(ArrayUtils.toObject(start.monthsDiff(end.getMonth()))));
      } else if (spansManyYears && isFirstYear && !isLastYear) {
        months.addAll(Arrays.asList(ArrayUtils.toObject(start.monthsDiff(12))));
      } else if (spansManyYears && !isFirstYear && !isLastYear) {
        months.addAll((List<Integer>) IntStream.rangeClosed(1, 12));
      } else {
        months.addAll((List<Integer>) IntStream.rangeClosed(1, end.getMonth()));
      }
      ret.put(y, months);
    });
    return ret;
  }

}
