package com.learning.journalApp.api.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WeatherResponseTest {

    @Test
    void testNoArgsConstructor() {
        WeatherResponse weatherResponse = new WeatherResponse();
        assertNull(weatherResponse.getCity());
        assertNull(weatherResponse.getTemperature());
        assertNull(weatherResponse.getDescription());
        assertNull(weatherResponse.getHumidity());
        assertNull(weatherResponse.getWindSpeed());
    }

    @Test
    void testGettersAndSetters() {
        WeatherResponse weatherResponse = new WeatherResponse();
        String city = "London";
        String temperature = "20°C";
        String description = "Partly Cloudy";
        String humidity = "80%";
        String windSpeed = "10 km/h";

        weatherResponse.setCity(city);
        weatherResponse.setTemperature(temperature);
        weatherResponse.setDescription(description);
        weatherResponse.setHumidity(humidity);
        weatherResponse.setWindSpeed(windSpeed);

        assertEquals(city, weatherResponse.getCity());
        assertEquals(temperature, weatherResponse.getTemperature());
        assertEquals(description, weatherResponse.getDescription());
        assertEquals(humidity, weatherResponse.getHumidity());
        assertEquals(windSpeed, weatherResponse.getWindSpeed());
    }
}
