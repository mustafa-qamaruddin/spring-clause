package com.qubits.task.services;

import com.qubits.task.models.dtos.Flight;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.qubits.task.configs.Constants.MAX_TRANSIT_WAIT_MIN;
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
    Map<Route, List<Schedule>> routesSchedsFirst = new HashMap<>();
    Map<Route, List<Schedule>> routesSchedsSecond = new HashMap<>();
    possibleRoutes.forEach(r -> {
      if (Objects.isNull(r.getConnectingAirport())) {
        routesSchedsFirst.put(r, getSchedules(r.getAirportFrom(), r.getAirportTo(), yearMonthPairs, departureDate, arrivalDate));
      } else {
        // For interconnected flights the difference between the arrival and the next departure
        // should be 2h or greater
        routesSchedsFirst.put(r, getSchedules(r.getAirportFrom(), r.getConnectingAirport(), yearMonthPairs, departureDate, arrivalDate));
        routesSchedsSecond.put(r, getSchedules(r.getConnectingAirport(), r.getAirportTo(), yearMonthPairs, departureDate, arrivalDate));
      }
    });
    var interconnections = mapScheduledToInterconnections(routesSchedsFirst);
    // add second leg
    loopCreateInterconnections(routesSchedsSecond, interconnections, false);
    // filter out the visited ones and surely the transits without second leg
    return interconnections.stream()
        .filter(interconnection -> !interconnection.isTransit() || (interconnection.isTransit()
            && !interconnection.isVisited() && interconnection.getStops() > 0 && interconnection.getLegs().size() == 2))
        .collect(Collectors.toList());
  }

  private List<Interconnection> mapScheduledToInterconnections(Map<Route, List<Schedule>> routesScheds) {
    // only put first leg in this iteration
    List<Interconnection> interconnections = new ArrayList<>();
    loopCreateInterconnections(routesScheds, interconnections, true);
    return interconnections;
  }

  private void loopCreateInterconnections(Map<Route, List<Schedule>> routesScheds, List<Interconnection> interconnections,
                                          boolean isFirstLeg) {
    routesScheds.forEach((route, schedules) -> {
      // extract days
      schedules.forEach(sched -> {
        sched.getDays().forEach(day -> {
          // extract flights for day
          day.getFlights().forEach(flight -> {
            // create legs
            Leg leg = getLeg(route, flight);
            // assign legs to interconnection
            if (isFirstLeg) {
              Interconnection i = new Interconnection().addLeg(leg).setTransit(!Objects.isNull(route.getConnectingAirport()))
                  .setVisited(false);
              interconnections.add(i);
            } else {
              // now we are sure this is the second leg
              // but there could be more than one match
              // copy interconnection, mark it as visited, and then set second leg and add to list
              // finally in the caller function filter out the transits without second leg
              var validCandidates = interconnections.stream()
                  .filter(interconnection -> interconnection.isTransit() && interconnection.getLegs().size() == 1
                      && !interconnection.isVisited())
                  .filter(interconnection -> {
                    long diffInMins = (flight.getStitchedDepartureDateTime().getTime() -
                        interconnection.getLegs().get(0).getArrivalDateTimeRaw().getTime()) / (60 * 1000) % 60;
                    return diffInMins <= MAX_TRANSIT_WAIT_MIN;
                  })
                  .map(interconnection -> interconnection.setVisited(true))
                  .collect(Collectors.toList());
              validCandidates.forEach(interconnection -> interconnections.add(new Interconnection(interconnection)
                  .incrementStops().addLeg(leg).setVisited(false)));
            }
          });
        });
      });
    });
  }

  private Leg getLeg(Route route, Flight flight) {
    Leg leg = new Leg();
    leg.setDepartureAirport(route.getAirportFrom());
    leg.setArrivalAirport(route.getAirportTo());
    leg.setDepartureDateTime(timeZoneUtils.formatDate(flight.getStitchedDepartureDateTime()));
    leg.setArrivalDateTime(timeZoneUtils.formatDate(flight.getStitchedArrivalDateTime()));
    leg.setArrivalDateTimeRaw(flight.getStitchedArrivalDateTime());
    return leg;
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

  public List<Schedule> getSchedules(String airportFrom, String airportTo, Map<Integer, List<Integer>> yearMonthPairs,
                                     CustomDate desiredDepature, CustomDate desiredArrival) {
    var scheduleArrayList = new ArrayList<Schedule>();
    var params = new HashMap<>(Map.of("airportFrom", airportFrom, "airportTo", airportTo));
    yearMonthPairs.forEach((year, months) -> {
      var schedules = airlineClient.fetchSchedules(airportFrom, airportTo, year, months, params);
      schedules.forEach(sched -> {
        sched.getDays().forEach(day -> {
          var filteredFlights = day.getFlights().stream().map(flight -> {
                flight.setStitchedDepartureDateTime(timeZoneUtils.stitchDateParts(sched.getYear(), sched.getMonth(),
                        day.getDay(), flight.getDepartureTime()))
                    .setStitchedArrivalDateTime(timeZoneUtils.stitchDateParts(sched.getYear(), sched.getMonth(),
                        day.getDay(), flight.getArrivalTime()));
                return flight;
              }).filter(flight -> flight.getStitchedDepartureDateTime().compareTo(desiredDepature.getRawDate()) >= 0 &&
                  flight.getStitchedArrivalDateTime().compareTo(desiredArrival.getRawDate()) <= 0)
              .collect(Collectors.toList());
          day.setFlights(filteredFlights);
        });
      });
      scheduleArrayList.addAll(schedules);
    });
    return scheduleArrayList;
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
