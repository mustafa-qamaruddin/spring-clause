package com.qubits.task.repositories;

import com.qubits.task.models.CompositeKey;
import com.qubits.task.models.SecretSanta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecretSantaRepository extends JpaRepository<SecretSanta, CompositeKey> {
}
