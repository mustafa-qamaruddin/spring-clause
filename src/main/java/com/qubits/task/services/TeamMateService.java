package com.qubits.task.services;

import com.qubits.task.models.TeamMate;
import com.qubits.task.repositories.TeamMateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamMateService {

  @Autowired
  private TeamMateRepository teamMateRepository;

  public TeamMate save(TeamMate newTeamMate) {
    TeamMate teamMate = teamMateRepository.save(newTeamMate);
    // assign first available santa for next year
    return teamMate;
  }
}
