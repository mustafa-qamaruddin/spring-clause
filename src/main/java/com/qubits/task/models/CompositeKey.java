package com.qubits.task.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class CompositeKey implements Serializable {
  private TeamMate giftGiver;
  private TeamMate giftReceiver;
  private int year;
}
