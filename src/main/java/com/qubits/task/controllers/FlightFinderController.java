package com.qubits.task.controllers;

import com.qubits.task.models.Interconnection;
import com.qubits.task.services.FlightFinderService;
import com.qubits.task.utils.ValidationUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
public class FlightFinderController {
  private final FlightFinderService flightFinderService;
  private final ValidationUtils validationUtils;

  public FlightFinderController(FlightFinderService flightFinderService, ValidationUtils validationUtils) {
    this.flightFinderService = flightFinderService;
    this.validationUtils = validationUtils;
  }

  @GetMapping("/api/v1/interconnections")
  public List<Interconnection> interconnections(
      @RequestParam(required = true) String departure,
      @RequestParam(required = true) String arrival,
      @RequestParam(required = true) String departureDateTime,
      @RequestParam(required = true) String arrivalDateTime
  ) {
    // validation on IATA codes
    // validation on datetimes
    var departureDate = validationUtils.validateDateTime(departureDateTime);
    var arrivalDate = validationUtils.validateDateTime(arrivalDateTime);
    // handle exceptions with CustomExceptionHandler the RESTful way
    // fetch data
    return flightFinderService.findInterconnections(
        departure,
        arrival,
        departureDate,
        arrivalDate
    );
  }
}

