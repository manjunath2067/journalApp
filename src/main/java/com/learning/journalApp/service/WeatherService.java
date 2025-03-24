package com.learning.journalApp.service;

import com.learning.journalApp.api.response.WeatherResponse;
import com.learning.journalApp.cache.AppCache;
import com.learning.journalApp.client.WeatherClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    private final WeatherClient weatherClient;
    private final AppCache appCache;

    @Autowired
    private RedisService redisService;

    public WeatherService(WeatherClient weatherClient, AppCache appCache) {
        this.weatherClient = weatherClient;
        this.appCache = appCache;
    }

    /**
     * Retrieves the weather information for a given city.
     * If the weather information is not found in Redis, it fetches from the API and stores it in Redis.
     *
     * @param city the name of the city to get the weather information for
     * @return the weather information for the specified city
     */
    public WeatherResponse getWeather(String city) {
        WeatherResponse weatherResponse = redisService.get("Weather_of_" + city, WeatherResponse.class);
        if (weatherResponse == null) {
            String weatherApiKey = appCache.getConfigValue(String.valueOf(AppCache.keys.WEATHER_API_KEY));
            weatherResponse = getWeatherFromApi(city, weatherApiKey);
            redisService.set("Weather_of_" + city, weatherResponse, 300L);
        }
        return weatherResponse;
    }

    private WeatherResponse getWeatherFromApi(String city, String apiKey) {
        return weatherClient.getWeather(apiKey, city);
    }

}
