package com.qubits.task.models;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

@Entity
@Data
public class TeamMate {

  @Id
  @GeneratedValue
  Long id;

  @NotNull
  @Column(unique = true)
  String email;

  @OneToMany
  SecretSanta[] giftReceivers;

  @OneToMany
  SecretSanta[] giftTakers;
}
