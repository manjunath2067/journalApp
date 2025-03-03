package com.learning.journalApp.service;

import com.learning.journalApp.api.response.WeatherResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WeatherService {

    private final static String weatherApiKey = "b53c7c7988f652a9c4e592b3acc9c1ed";

    private final static String API = "http://api.weatherstack.com/current?access_key=API_KEY&query=CITY";

    @Autowired
    private RestTemplate restTemplate;

    public WeatherResponse getWeather(String city) {
        String finalAPI = API.replace("CITY", city).replace("API_KEY", weatherApiKey);
        ResponseEntity<WeatherResponse> responseEntity = restTemplate.exchange(finalAPI, HttpMethod.GET, null, WeatherResponse.class);
        return responseEntity.getBody();

    }
}