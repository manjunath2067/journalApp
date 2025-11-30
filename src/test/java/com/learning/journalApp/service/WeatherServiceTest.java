package com.learning.journalApp.service;

import com.learning.journalApp.client.WeatherClient;
import com.learning.journalApp.api.response.WeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class WeatherServiceTest {

    @Mock
    private WeatherClient weatherClient;

    @InjectMocks
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetWeather_Success() {
        // Arrange
        String city = "London";
        WeatherResponse mockWeatherResponse = new WeatherResponse();
        WeatherResponse.CurrentWeather currentWeather = new WeatherResponse.CurrentWeather();
        currentWeather.setTemperature(15.0);
        currentWeather.setWeathercode(1);
        mockWeatherResponse.setCurrentWeather(currentWeather);

        when(weatherClient.getWeather(city)).thenReturn(mockWeatherResponse);

        // Act
        WeatherResponse result = weatherService.getWeather(city);

        // Assert
        assertNotNull(result);
        assertEquals(15.0, result.getCurrentWeather().getTemperature());
        assertEquals(1, result.getCurrentWeather().getWeathercode());
    }

    @Test
    void testGetWeather_ClientReturnsNull() {
        // Arrange
        String city = "NonExistentCity";
        when(weatherClient.getWeather(city)).thenReturn(null);

        // Act
        WeatherResponse result = weatherService.getWeather(city);

        // Assert
        assertEquals(null, result);
    }
}
