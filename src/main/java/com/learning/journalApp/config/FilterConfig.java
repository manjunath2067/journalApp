package com.learning.journalApp.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<Bucket4jRateLimitFilter> rateLimitFilterRegistration(Bucket4jRateLimitFilter filter) {
        FilterRegistrationBean<Bucket4jRateLimitFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        // This mapping ensures the filter only applies to /public/create-user
        registration.addUrlPatterns("/public/create-user");
        registration.setOrder(1); // Set the order if you have multiple filters
        return registration;
    }
}
