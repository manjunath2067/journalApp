package com.learning.journalApp.client;

import com.learning.journalApp.api.response.WeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class WeatherClientTest {

    @InjectMocks
    private WeatherClient weatherClient;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetWeather() {
        // Arrange
        String city = "London";
        WeatherResponse mockResponse = new WeatherResponse();
        mockResponse.setCity(city);
        mockResponse.setTemperature("20C");

        when(restTemplate.getForObject(any(String.class), eq(WeatherResponse.class), any(String.class)))
                .thenReturn(mockResponse);

        // Act
        WeatherResponse weatherResponse = weatherClient.getWeather(city);

        // Assert
        assertNotNull(weatherResponse);
        assertNotNull(weatherResponse.getCity());
        assertNotNull(weatherResponse.getTemperature());
    }
}
