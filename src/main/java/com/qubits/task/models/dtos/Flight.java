package com.qubits.task.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Flight {
  @JsonProperty("carrierCode")
  private String carrierCode;
  @JsonProperty("number")
  private String number;
  @JsonProperty("departureTime")
  private String departureTime;
  @JsonProperty("arrivalTime")
  private String arrivalTime;
  @JsonIgnore
  private Date stitchedDepartureDateTime;
  @JsonIgnore
  private Date stitchedArrivalDateTime;

  public Date getStitchedDepartureDateTime() {
    return stitchedDepartureDateTime;
  }

  public Flight setStitchedDepartureDateTime(Date stitchedDepartureDateTime) {
    this.stitchedDepartureDateTime = stitchedDepartureDateTime;
    return this;
  }

  public Date getStitchedArrivalDateTime() {
    return stitchedArrivalDateTime;
  }

  public Flight setStitchedArrivalDateTime(Date stitchedArrivalDateTime) {
    this.stitchedArrivalDateTime = stitchedArrivalDateTime;
    return this;
  }
}