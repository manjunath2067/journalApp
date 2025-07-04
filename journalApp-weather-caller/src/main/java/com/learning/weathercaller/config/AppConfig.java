package com.learning.weathercaller.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Value("${journalapp.weather.url}")
    private String journalAppWeatherUrl;

    @Bean
    public WebClient webClient() {
        // TODO: Add authentication headers if journalApp's /weather endpoint requires them.
        // This might involve:
        // 1. Basic Auth: .defaultHeaders(header -> header.setBasicAuth("username", "password"))
        // 2. Bearer Token: .defaultHeaders(header -> header.setBearerAuth("your-jwt-token"))
        // 3. API Key: .defaultHeader("X-API-KEY", "your-api-key")
        // The actual authentication mechanism will depend on how journalApp's /weather endpoint is secured.
        return WebClient.builder()
                .baseUrl(journalAppWeatherUrl)
                .defaultHeader("Content-Type", "application/json") // Assuming it might be useful, though for GET not strictly needed for request
                // .defaultHeader("Authorization", "Bearer <token>") // Placeholder for auth
                .build();
    }
}
