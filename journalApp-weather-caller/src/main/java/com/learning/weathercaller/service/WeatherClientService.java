package com.learning.weathercaller.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class WeatherClientService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherClientService.class);

    private final WebClient webClient;

    @Autowired
    public WeatherClientService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<String> fetchWeatherFromJournalApp() {
        // The journalApp's /weather endpoint is GET and its URL is configured as the base URL for WebClient.
        // Authentication:
        // The journalApp's WeatherController uses SecurityContextHolder.getContext().getAuthentication().
        // This means the /weather endpoint is secured.
        // This WebClient call will likely fail with a 401 or 403 if journalApp
        // is running with security enabled and no authentication is provided here.
        // The actual authentication mechanism (e.g., Basic Auth, OAuth2 client credentials)
        // needs to be implemented in the WebClient configuration (see AppConfig.java)
        // or passed dynamically if the credentials vary per request.

        logger.info("Attempting to call journalApp's weather service via WebClient.");

        return webClient.get()
                // .uri("/") // If base URL is exactly "http://localhost:8080/weather", then path is "/"
                            // If base URL is "http://localhost:8080", then path is "/weather"
                            // Based on current AppConfig, base URL is the full path, so "/" or empty is fine.
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> logger.info("Successfully received response from journalApp weather service."))
                .doOnError(error -> logger.error("Error calling journalApp weather service: {}", error.getMessage()));
    }
}
