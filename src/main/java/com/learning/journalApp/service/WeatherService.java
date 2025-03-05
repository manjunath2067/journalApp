package com.learning.journalApp.service;

import com.learning.journalApp.api.response.WeatherResponse;
import com.learning.journalApp.client.WeatherClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    @Value("${weather.api.key:b53c7c7988f652a9c4e592b3acc9c1ed}")
    private String weatherApiKey;

    private final WeatherClient weatherClient;

    public WeatherService(WeatherClient weatherClient) {
        this.weatherClient = weatherClient;
    }

    public WeatherResponse getWeather(String city) {
        return weatherClient.getWeather(weatherApiKey, city);
    }

}
