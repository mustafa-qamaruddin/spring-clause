package com.qubits.task.controllers;

import com.qubits.task.models.pojos.Interconnection;
import com.qubits.task.services.FlightFinderService;
import com.qubits.task.utils.SearchFormUtils;
import com.qubits.task.utils.constraints.DateTimeFormatConstraint;
import com.qubits.task.utils.constraints.DateTimeFutureConstraint;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/interconnections")
@Validated
public class FlightFinderController {
  private final FlightFinderService flightFinderService;
  private final SearchFormUtils searchFormUtils;

  public FlightFinderController(FlightFinderService flightFinderService, SearchFormUtils searchFormUtils) {
    this.flightFinderService = flightFinderService;
    this.searchFormUtils = searchFormUtils;
  }

  @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  @Validated
  public List<Interconnection> interconnections(
      @RequestParam(required = true) String departure,
      @RequestParam(required = true) String arrival,
      @Valid @DateTimeFormatConstraint @DateTimeFutureConstraint @RequestParam(required = true) String departureDateTime,
      @Valid @DateTimeFormatConstraint @DateTimeFutureConstraint @RequestParam(required = true) String arrivalDateTime
  ) {
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

