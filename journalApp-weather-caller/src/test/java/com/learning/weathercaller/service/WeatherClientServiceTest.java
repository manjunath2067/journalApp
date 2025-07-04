package com.learning.weathercaller.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherClientServiceTest {

    @Mock
    private WebClient webClientMock;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @InjectMocks
    private WeatherClientService weatherClientService;

    @BeforeEach
    void setUp() {
        // Basic setup for WebClient mock chain
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.retrieve()).thenReturn(responseSpecMock);
    }

    @Test
    void fetchWeatherFromJournalApp_success() {
        String expectedResponse = "Hi testuser, Weather feels like: 25C in TestCity";
        when(responseSpecMock.bodyToMono(String.class)).thenReturn(Mono.just(expectedResponse));

        Mono<String> resultMono = weatherClientService.fetchWeatherFromJournalApp();

        StepVerifier.create(resultMono)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).retrieve();
        verify(responseSpecMock).bodyToMono(String.class);
    }

    @Test
    void fetchWeatherFromJournalApp_error() {
        RuntimeException simulatedError = new RuntimeException("Connection refused");
        when(responseSpecMock.bodyToMono(String.class)).thenReturn(Mono.error(simulatedError));

        Mono<String> resultMono = weatherClientService.fetchWeatherFromJournalApp();

        StepVerifier.create(resultMono)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                                                 throwable.getMessage().equals("Connection refused"))
                .verify();

        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).retrieve();
        verify(responseSpecMock).bodyToMono(String.class);
    }
}
