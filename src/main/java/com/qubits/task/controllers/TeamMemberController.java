package com.qubits.task.controllers;

import com.qubits.task.models.TeamMate;
import com.qubits.task.services.TeamMateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TeamMemberController {

  @Autowired
  TeamMateService teamMateService;

  @PostMapping(
      path = "/create",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<TeamMate> create(@RequestBody TeamMate newTeamMate) {
    return new ResponseEntity<TeamMate>(teamMateService.save(newTeamMate), HttpStatus.CREATED);
  }
}
