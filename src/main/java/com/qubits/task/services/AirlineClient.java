package com.qubits.task.services;

import com.qubits.task.models.Route;
import com.qubits.task.models.Schedule;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    var response = routesWebClient.get().uri("/locate/3/routes/" + airportFrom).retrieve().onStatus(
        status -> status.value() == 401,
        clientResponse -> Mono.empty()
    ).bodyToMono(new ParameterizedTypeReference<List<Route>>() {
    }).block();

    // @todo error handling
    // @todo handle not found error
    // @todo validation on IATA codes

    if (response != null) {
      cachedRoutes.put(airportFrom, response);
      cacheLastUpdated.put(airportFrom, System.currentTimeMillis());
      return cachedRoutes.get(airportFrom);
    } else {
      throw new HttpClientErrorException(HttpStatus.SERVICE_UNAVAILABLE, "Failed to fetch routes list");
    }
  }

  public List<Schedule> fetchSchedules() {
    return null;
  }
}
