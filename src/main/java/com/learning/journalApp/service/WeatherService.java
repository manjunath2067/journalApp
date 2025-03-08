package com.learning.journalApp.service;

import com.learning.journalApp.api.response.WeatherResponse;
import com.learning.journalApp.cache.AppCache;
import com.learning.journalApp.client.WeatherClient;

import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    private final WeatherClient weatherClient;
    private final AppCache appCache;

    public WeatherService(WeatherClient weatherClient, AppCache appCache) {
        this.weatherClient = weatherClient;
        this.appCache = appCache;
    }

    public WeatherResponse getWeather(String city) {
        String weatherApiKey = appCache.getConfigValue(String.valueOf(AppCache.keys.WEATHER_API_KEY));
        return weatherClient.getWeather(weatherApiKey, city);
    }

}
