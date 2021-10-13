package com.qubits.task.repositories;

import com.qubits.task.models.TeamMate;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamMateRepository extends PagingAndSortingRepository<TeamMate, Long> {
}
