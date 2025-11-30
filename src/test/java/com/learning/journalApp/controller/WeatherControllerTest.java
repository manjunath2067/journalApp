package com.learning.journalApp.controller;

import com.learning.journalApp.api.response.WeatherResponse;
import com.learning.journalApp.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class WeatherControllerTest {

    @InjectMocks
    private WeatherController weatherController;

    @Mock
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetWeather_success() {
        // Arrange
        String city = "London";
        WeatherResponse mockResponse = new WeatherResponse();
        mockResponse.setCity(city);
        mockResponse.setTemperature("20°C");

        when(weatherService.getWeather(anyString())).thenReturn(mockResponse);

        // Act
        ResponseEntity<WeatherResponse> response = weatherController.getWeather(city);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void testGetWeather_serviceReturnsNull() {
        // Arrange
        String city = "Unknown";
        when(weatherService.getWeather(anyString())).thenReturn(null);

        // Act
        ResponseEntity<WeatherResponse> response = weatherController.getWeather(city);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetWeather_exception() {
        // Arrange
        String city = "ErrorCity";
        when(weatherService.getWeather(anyString())).thenThrow(new RuntimeException("API error"));

        // Act
        ResponseEntity<WeatherResponse> response = weatherController.getWeather(city);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
