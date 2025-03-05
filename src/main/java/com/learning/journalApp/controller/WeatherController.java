package com.learning.journalApp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learning.journalApp.api.response.WeatherResponse;
import com.learning.journalApp.service.WeatherService;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public ResponseEntity<?> greeting() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Call the weather service via OpenFeign
        WeatherResponse weatherResponse = weatherService.getWeather("Bangalore");
        String greeting = "";
        if (weatherResponse != null) {
            greeting = ", Weather feels like: "
                  + weatherResponse.getCurrent().getFeelsLike()
                  + " in "
                  + weatherResponse.getLocation().getName();
        }
        return ResponseEntity.ok("Hi " + authentication.getName() + greeting);
    }

}
