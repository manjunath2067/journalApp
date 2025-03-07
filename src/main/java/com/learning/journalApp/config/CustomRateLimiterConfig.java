package com.learning.journalApp.config;

import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CustomRateLimiterConfig {

    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        RateLimiterConfig config = RateLimiterConfig.custom()
              .limitForPeriod(5)
              .limitRefreshPeriod(Duration.ofSeconds(60))
              .timeoutDuration(Duration.ofSeconds(2))
              .build();
        return RateLimiterRegistry.of(config);
    }
}
