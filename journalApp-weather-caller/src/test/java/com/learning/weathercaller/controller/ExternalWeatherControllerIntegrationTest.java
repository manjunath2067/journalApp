package com.learning.weathercaller.controller;

import com.learning.weathercaller.service.WeatherClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

// Using WebFluxTest as we are using WebFlux (WebClient) and reactive controller
@WebFluxTest(ExternalWeatherController.class)
public class ExternalWeatherControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean // Mocks the WeatherClientService in the Spring application context
    private WeatherClientService weatherClientService;

    @Test
    void getJournalAppData_success() {
        String mockServiceResponse = "Hi from JournalApp!";
        when(weatherClientService.fetchWeatherFromJournalApp()).thenReturn(Mono.just(mockServiceResponse));

        webTestClient.get().uri("/fetch-journal-weather")
                .accept(MediaType.TEXT_PLAIN) // The controller returns ResponseEntity<String>
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo(mockServiceResponse);
    }

    @Test
    void getJournalAppData_serviceError() {
        String errorMessage = "Service unavailable";
        when(weatherClientService.fetchWeatherFromJournalApp()).thenReturn(Mono.error(new RuntimeException(errorMessage)));

        webTestClient.get().uri("/fetch-journal-weather")
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus().is5xxServerError() // Expecting INTERNAL_SERVER_ERROR
                .expectBody(String.class).isEqualTo("Error fetching weather data from JournalApp: " + errorMessage);
    }
}
