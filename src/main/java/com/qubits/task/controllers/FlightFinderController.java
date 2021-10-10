package com.qubits.task.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
public class FlightFinderController {
  @GetMapping("/api/v1/interconnections")
  public List<Objects> interconnections(
      @RequestParam(required = true) String departure,
      @RequestParam(required = true) String arrival,
      @RequestParam(required = true) String departureDateTime,
      @RequestParam(required = true) String arrivalDateTime
  ) {
    return new ArrayList<>();
  }
}

