package com.qubits.task.services;

import com.qubits.task.exceptions.ThirdPartyErrorException;
import com.qubits.task.models.dtos.Route;
import com.qubits.task.models.dtos.Schedule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class AirlineClient {
  private final WebClient routesWebClient;
  private final WebClient schedulesWebClient;
  private Map<String, List<Route>> cachedRoutes;
  private Map<String, Long> cacheLastUpdated;
  private long TWO_HOURS_MILLIS = 432000000;

  public AirlineClient(WebClient routesWebClient, WebClient schedulesWebClient) {
    this.routesWebClient = routesWebClient;
    this.schedulesWebClient = schedulesWebClient;
    cachedRoutes = new HashMap<>();
    cacheLastUpdated = new HashMap<>();
  }

  public List<Route> fetchRoutes(String airportFrom) throws HttpClientErrorException {
    if (cachedRoutes != null && !cachedRoutes.isEmpty() && cachedRoutes.get(airportFrom) != null &&
        cacheLastUpdated.get(airportFrom) != null &&
        cacheLastUpdated.get(airportFrom) + TWO_HOURS_MILLIS < System.currentTimeMillis()) {
      return cachedRoutes.get(airportFrom);
    }
    var params = Map.of("airportFrom", airportFrom);
    var endpoint = "/locate/3/routes/";
    var response = routesWebClient.get().uri(endpoint + airportFrom).retrieve()
        .onStatus(
            HttpStatus::is5xxServerError,
            clientResponse -> {
              log.error("Failed to fetch routes list" + clientResponse.toString());
              return Mono.error(new ThirdPartyErrorException("Failed to fetch routes list", params));
            }
        ).onStatus(
            HttpStatus::is4xxClientError,
            clientResponse -> {
              log.error("Invalid parameters used on routes list" + clientResponse.toString());
              return Mono.error(new ThirdPartyErrorException("Invalid parameters used on routes list", params));
            }
        ).onStatus(
            HttpStatus::is3xxRedirection,
            clientResponse -> {
              log.error("Contact support, routes APIs changed" + clientResponse.toString());
              return Mono.error(new ThirdPartyErrorException("Contact support, routes APIs changed", params));
            }
        ).bodyToMono(new ParameterizedTypeReference<List<Route>>() {
        }).block();

    if (response != null && !response.isEmpty()) {
      cachedRoutes.put(airportFrom, response);
      cacheLastUpdated.put(airportFrom, System.currentTimeMillis());
      return cachedRoutes.get(airportFrom);
    } else {
      log.error("No routes exist" + params);
      throw new ThirdPartyErrorException("No routes exist", params);
    }
  }

  public List<Schedule> fetchSchedules(String airportFrom, String airportTo, int year, List<Integer> months,
                                       Map<String, String> params) {
    var endpoint = "/3/schedules/{airportFrom}/{airportTo}/years/{year}/months/{year}";
    params.put("year", String.valueOf(year));
    List<Schedule> response = new ArrayList<>();
    months.forEach(month -> {
      params.put("month", String.valueOf(month));
      response.add(getSchedulesBetween(airportFrom, airportTo, year, month, endpoint, params).setYear(year));
    });
    return response;
  }

  private Schedule getSchedulesBetween(String airportFrom, String airportTo, int year, int month, String endpoint,
                                       Map<String, String> params) {
    return schedulesWebClient.get().uri(endpoint, airportFrom, airportTo, year, month).retrieve()
        .onStatus(
            HttpStatus::is5xxServerError,
            clientResponse -> {
              log.error("Failed to fetch schedules list" + clientResponse.toString());
              return Mono.error(new ThirdPartyErrorException("Failed to fetch schedules list", params));
            }
        ).onStatus(
            HttpStatus::is4xxClientError,
            clientResponse -> {
              log.error("Invalid parameters used on schedules list" + clientResponse.toString());
              return Mono.error(new ThirdPartyErrorException("Invalid parameters used on schedules list", params));
            }
        ).onStatus(
            HttpStatus::is3xxRedirection,
            clientResponse -> {
              log.error("Contact support, schedules APIs changed" + clientResponse.toString());
              return Mono.error(new ThirdPartyErrorException("Contact support, schedules APIs changed", params));
            }
        ).bodyToMono(Schedule.class).block();
  }
}
