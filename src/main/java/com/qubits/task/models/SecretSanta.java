package com.qubits.task.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@IdClass(CompositeKey.class)
public class SecretSanta {
  @ManyToOne
  @Id
  TeamMate giftGiver;
  @ManyToOne
  @Id
  TeamMate giftReceiver;
  @Id
  int year;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    SecretSanta that = (SecretSanta) o;
    return giftGiver != null && Objects.equals(giftGiver, that.giftGiver)
        && giftReceiver != null && Objects.equals(giftReceiver, that.giftReceiver)
        && year != 0 && Objects.equals(year, that.year);
  }

  @Override
  public int hashCode() {
    return Objects.hash(giftGiver, giftReceiver, year);
  }
}
