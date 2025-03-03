package com.learning.journalApp.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WeatherResponse {
    private Location location;
    private Current current;

    @Getter
    @Setter
    public static class Current {

        private int temperature;

        @JsonProperty("weather_descriptions")
        private List<String> weatherDescriptions;

        @JsonProperty("wind_speed")
        private int windSpeed;

        @JsonProperty("wind_dir")
        private String windDir;

        private int pressure;

        private int humidity;

        @JsonProperty("feelslike")
        private int feelsLike;

        private int visibility;

        @JsonProperty("is_day")
        private String isDay;
    }

    @Getter
    @Setter
    public static class Location {
        private String name;

        private String country;
    }
}