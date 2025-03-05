package com.learning.journalApp.client;

import com.learning.journalApp.api.response.WeatherResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "weatherClient", url = "http://api.weatherstack.com")
public interface WeatherClient {

    @GetMapping("/current")
    WeatherResponse getWeather(
          @RequestParam("access_key") String apiKey,
          @RequestParam("query") String city
    );
}
