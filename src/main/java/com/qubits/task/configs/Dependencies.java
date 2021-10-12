package com.qubits.task.configs;

import com.qubits.task.services.AirlineClient;
import com.qubits.task.services.FlightFinderService;
import com.qubits.task.utils.TimeZoneUtils;
import com.qubits.task.utils.SearchFormUtils;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.validation.Validator;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Configuration
@ConfigurationProperties(prefix = "airlines")
public class Dependencies {
  private String timezone;
  private String routesAPI;
  private String schedulesAPI;

  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }

  public String getRoutesAPI() {
    return routesAPI;
  }

  public void setRoutesAPI(String routesAPI) {
    this.routesAPI = routesAPI;
  }

  public String getSchedulesAPI() {
    return schedulesAPI;
  }

  public void setSchedulesAPI(String schedulesAPI) {
    this.schedulesAPI = schedulesAPI;
  }

  @Bean
  public FlightFinderService flightFinderService() {
    return new FlightFinderService(airlineClient(), timeZoneUtils());
  }

  @Bean
  public AirlineClient airlineClient() {
    return new AirlineClient(routesWebClient(), schedulesWebClient());
  }

  @Bean
  public SearchFormUtils validationUtils() {
    return new SearchFormUtils(timeZoneUtils());
  }

  @Bean
  WebClient routesWebClient() {
    return WebClient.builder()
        .baseUrl(routesAPI)
        .defaultHeader(ACCEPT, APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean
  WebClient schedulesWebClient() {
    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(httpClient()))
        .baseUrl(schedulesAPI)
        .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .defaultUriVariables(Collections.singletonMap("url", schedulesAPI))
        .build();
  }

  @Bean
  HttpClient httpClient() {
    return HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
        .option(ChannelOption.SO_KEEPALIVE, true)
        .responseTimeout(Duration.ofMillis(5000))
        .doOnConnected(conn ->
            conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS))
        );
  }

  @Bean
  TimeZoneUtils timeZoneUtils() {
    return new TimeZoneUtils(timezone);
  }

  @Bean
  public Validator validator() {
    return new LocalValidatorFactoryBean();
  }

  @Bean
  public MethodValidationPostProcessor methodValidationPostProcessor() {
    MethodValidationPostProcessor methodValidationPostProcessor = new MethodValidationPostProcessor();
    methodValidationPostProcessor.setValidator(validator());
    return methodValidationPostProcessor;
  }
}
