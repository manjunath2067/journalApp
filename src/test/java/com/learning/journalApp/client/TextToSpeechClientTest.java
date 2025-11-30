package com.learning.journalApp.client;

import com.learning.journalApp.entity.TextToSpeechRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class TextToSpeechClientTest {

    @InjectMocks
    private TextToSpeechClient textToSpeechClient;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTextToSpeech() {
        // Arrange
        String text = "Hello world";
        TextToSpeechRequest request = new TextToSpeechRequest();
        request.setText(text);
        request.setVoiceId("someVoiceId");

        ResponseEntity<byte[]> mockResponse = new ResponseEntity<>(new byte[0], HttpStatus.OK);
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.POST), any(HttpEntity.class), eq(byte[].class)))
                .thenReturn(mockResponse);

        // Act
        byte[] audioBytes = textToSpeechClient.getTextToSpeech(request);

        // Assert
        assertNotNull(audioBytes);
    }
}
