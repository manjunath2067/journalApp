package com.learning.weathercaller.controller;

import com.learning.weathercaller.service.WeatherClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fetch-journal-weather") // Endpoint for this new microservice
public class ExternalWeatherController {

    private static final Logger logger = LoggerFactory.getLogger(ExternalWeatherController.class);
    private final WeatherClientService weatherClientService;

    @Autowired
    public ExternalWeatherController(WeatherClientService weatherClientService) {
        this.weatherClientService = weatherClientService;
    }

    @GetMapping
    public Mono<ResponseEntity<String>> getJournalAppData() {
        logger.info("Received request for /fetch-journal-weather endpoint.");
        return weatherClientService.fetchWeatherFromJournalApp()
                .map(response -> {
                    logger.info("Successfully fetched data from journalApp, returning to client.");
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(e -> {
                    logger.error("Error encountered while fetching from journalApp: {}", e.getMessage());
                    // Depending on the error, you might want to return different status codes.
                    // For now, returning a generic 500 Internal Server Error.
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error fetching weather data from JournalApp: " + e.getMessage()));
                });
    }
}
