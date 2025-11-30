package com.learning.journalApp.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Bucket4jRateLimitFilterTest {

    @InjectMocks
    private Bucket4jRateLimitFilter bucket4jRateLimitFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();

        // Initialize the filter with a test bucket
        Bucket testBucket = Bucket4j.builder()
                .addLimit(Bandwidth.classic(1, Refill.greedy(1, Duration.ofMinutes(1))))
                .build();
        bucket4jRateLimitFilter = new Bucket4jRateLimitFilter(testBucket);
    }

    @Test
    void testDoFilterInternal_allowRequest() throws ServletException, IOException {
        // Arrange
        request.setRequestURI("/public/home");

        // Act
        bucket4jRateLimitFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    void testDoFilterInternal_rateLimitExceeded() throws ServletException, IOException {
        // Arrange
        request.setRequestURI("/public/home");

        // Consume the initial token
        bucket4jRateLimitFilter.doFilterInternal(request, response, filterChain);

        // Act - Second request should be rate limited
        MockHttpServletResponse secondResponse = new MockHttpServletResponse();
        bucket4jRateLimitFilter.doFilterInternal(request, secondResponse, filterChain);

        // Assert
        assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), secondResponse.getStatus());
    }

    @Test
    void testDoFilterInternal_nonRateLimitedPath() throws ServletException, IOException {
        // Arrange
        request.setRequestURI("/journal"); // A path not subject to rate limiting

        // Act
        bucket4jRateLimitFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }
}
