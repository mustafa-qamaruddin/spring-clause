package com.qubits.task.controllers;

import com.qubits.task.models.Interconnection;
import com.qubits.task.services.FlightFinderService;
import com.qubits.task.utils.SearchFormUtils;
import com.qubits.task.utils.constraints.DateTimeFormatConstraint;
import com.qubits.task.utils.constraints.DateTimeFutureConstraint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
public class FlightFinderController {
  private final FlightFinderService flightFinderService;
  private final SearchFormUtils searchFormUtils;

  public FlightFinderController(FlightFinderService flightFinderService, SearchFormUtils searchFormUtils) {
    this.flightFinderService = flightFinderService;
    this.searchFormUtils = searchFormUtils;
  }

  @GetMapping("/api/v1/interconnections")
  public List<Interconnection> interconnections(
      @RequestParam(required = true) String departure,
      @RequestParam(required = true) String arrival,
      @RequestParam(required = true) @Valid @DateTimeFormatConstraint @DateTimeFutureConstraint String departureDateTime,
      @RequestParam(required = true) @Valid @DateTimeFormatConstraint @DateTimeFutureConstraint String arrivalDateTime
  ) {
    // validation on datetimes
    var dates = searchFormUtils.parseDateTimes(Map.of("departure", departureDateTime, "arrival",
        arrivalDateTime));
    return flightFinderService.findInterconnections(
        departure,
        arrival,
        dates.get("departure"),
        dates.get("arrival")
    );
  }
}

