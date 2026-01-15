package com.learning.journalApp.service;

import com.learning.journalApp.api.response.WeatherResponse;
import com.learning.journalApp.cache.AppCache;
import com.learning.journalApp.client.WeatherClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTests {

    @InjectMocks
    private WeatherService weatherService;

    @Mock
    private WeatherClient weatherClient;

    @Mock
    private AppCache appCache;

    private final String TEST_CITY = "London";
    private final String TEST_API_KEY = "testApiKey123";

    @BeforeEach
    void setUp() {
        // Mocks are initialized by @ExtendWith(MockitoExtension.class)
    }

    @Test
    void testGetWeather_success() {
        // Arrange
        WeatherResponse mockResponse = new WeatherResponse();
        WeatherResponse.Current current = new WeatherResponse.Current();
        current.setTemperature(20); // Assuming temperature is int as per WeatherResponse.Current
        mockResponse.setCurrent(current);

        when(appCache.getConfigValue(AppCache.keys.WEATHER_API_KEY.name())).thenReturn(TEST_API_KEY);
        when(weatherClient.getWeather(TEST_API_KEY, TEST_CITY)).thenReturn(mockResponse);

        // Act
        WeatherResponse actualResponse = weatherService.getWeather(TEST_CITY);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(mockResponse, actualResponse);
        if (actualResponse.getCurrent() != null) { // Check if current part is set
            assertEquals(20, actualResponse.getCurrent().getTemperature());
        }
        verify(appCache, times(1)).getConfigValue(AppCache.keys.WEATHER_API_KEY.name());
        verify(weatherClient, times(1)).getWeather(TEST_API_KEY, TEST_CITY);
    }

    @Test
    void testGetWeather_apiKeyMissing_passesNullToClient() {
        // Arrange
        WeatherResponse mockResponse = new WeatherResponse(); // Client might return empty/error response
        when(appCache.getConfigValue(AppCache.keys.WEATHER_API_KEY.name())).thenReturn(null);
        // Assuming the client is called even with a null API key
        when(weatherClient.getWeather(null, TEST_CITY)).thenReturn(mockResponse);

        // Act
        WeatherResponse actualResponse = weatherService.getWeather(TEST_CITY);

        // Assert
        assertNotNull(actualResponse); // Or assert specific error response if client behaves that way
        assertEquals(mockResponse, actualResponse);
        verify(appCache, times(1)).getConfigValue(AppCache.keys.WEATHER_API_KEY.name());
        verify(weatherClient, times(1)).getWeather(null, TEST_CITY);
    }

    @Test
    void testGetWeather_weatherClientThrowsException_propagatesException() {
        // Arrange
        when(appCache.getConfigValue(AppCache.keys.WEATHER_API_KEY.name())).thenReturn(TEST_API_KEY);
        when(weatherClient.getWeather(TEST_API_KEY, TEST_CITY)).thenThrow(new RuntimeException("API client error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            weatherService.getWeather(TEST_CITY);
        });
        assertEquals("API client error", exception.getMessage());
        verify(appCache, times(1)).getConfigValue(AppCache.keys.WEATHER_API_KEY.name());
        verify(weatherClient, times(1)).getWeather(TEST_API_KEY, TEST_CITY);
    }

    @Test
    void testGetWeather_appCacheThrowsException_propagatesException() {
        // Arrange
        when(appCache.getConfigValue(AppCache.keys.WEATHER_API_KEY.name())).thenThrow(new RuntimeException("AppCache error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            weatherService.getWeather(TEST_CITY);
        });
        assertEquals("AppCache error", exception.getMessage());
        verify(appCache, times(1)).getConfigValue(AppCache.keys.WEATHER_API_KEY.name());
        verify(weatherClient, never()).getWeather(anyString(), anyString()); // Client should not be called
    }
}
