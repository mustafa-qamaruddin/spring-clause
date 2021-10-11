package com.qubits.task.services;

import com.qubits.task.models.pojos.Leg;
import com.qubits.task.utils.CustomDate;
import com.qubits.task.models.pojos.Interconnection;
import com.qubits.task.models.dtos.Route;
import com.qubits.task.models.dtos.Schedule;
import com.qubits.task.utils.TimeZoneUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
  private final TimeZoneUtils timeZoneUtils;

  public FlightFinderService(AirlineClient airlineClient, TimeZoneUtils timeZoneUtils) {
    this.airlineClient = airlineClient;
    this.timeZoneUtils = timeZoneUtils;
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
    possibleRoutes.forEach(r -> {
      if (Objects.isNull(r.getConnectingAirport())) { // @todo unnecessary condition, maybe here put the map(transitLeg1, transitLeg2)
        routesScheds.put(r, getSchedules(r.getAirportFrom(), r.getAirportTo(), yearMonthPairs));
      } else {
        // For interconnected flights the difference between the arrival and the next departure
        // should be 2h or greater
//        ???????????????????????????????????????????????????????????????????????????????????????
        routesScheds.put(r, getSchedules(r.getAirportFrom(), r.getAirportTo(), yearMonthPairs));
      }
    });
    return mapScheduledToInterconnections(routesScheds);
  }

  private List<Interconnection> mapScheduledToInterconnections(Map<Route, List<Schedule>> routesScheds) {
    List<Interconnection> interconnections = new ArrayList<>();
    routesScheds.forEach((route, schedules) -> {
      if (Objects.isNull(route.getConnectingAirport())) {
        // if isTransit? map find for a given route the other routes that start from it ( get the routes earlier to avoid nested loops)
        // condition no transit > 2 hours
        // extract flights and link to legs
      }
      // extract days
      schedules.forEach(sched -> {
        sched.getDays().forEach(day -> {
          // extract flights for day
          day.getFlights().forEach(flight -> {
            // create legs
            Leg leg = new Leg();
            leg.setDepartureAirport(route.getAirportFrom());
            leg.setArrivalAirport(route.getAirportTo());
            leg.setDepartureDateTime(timeZoneUtils.stitchDateParts(sched.getYear(), sched.getMonth(), day.getDay(),
                flight.getDepartureTime()));
            leg.setArrivalDateTime(timeZoneUtils.stitchDateParts(sched.getYear(), sched.getMonth(), day.getDay(),
                flight.getArrivalTime()));
            // assign legs to interconnection
            Interconnection i = new Interconnection().addLeg(leg);
            interconnections.add(i);
          });
        });
      });
      // @todo make sure results are properly sorted
    });
    return interconnections;
  }

  public List<Route> getPossibleRoutes(String airportFrom, String to) {
    // Please note that only routes with:
    // connectingAirport set to null and operator set to RYANAIR should be used
    return Optional.of(airlineClient.fetchRoutes(airportFrom))
        .orElse(Collections.emptyList())
        .stream()
        .filter(r -> Objects.equals(r.getOperator(), RYAN_AIR) && Objects.equals(r.getAirportTo(), to))
        .collect(Collectors.toList());
  }

  public List<Schedule> getSchedules(String airportFrom, String airportTo, Map<Integer, List<Integer>> yearMonthPairs) {
    var response = new ArrayList<Schedule>();
    var params = new HashMap<>(Map.of("airportFrom", airportFrom, "airportTo", airportTo));
    yearMonthPairs.forEach((year, months) -> response.addAll(airlineClient.fetchSchedules(airportFrom, airportTo, year,
        months, params)));
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
