package com.learning.journalApp.controller;

import com.learning.journalApp.entity.TextToSpeechRequest;
import com.learning.journalApp.service.TextToSpeechService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TextToSpeechControllerTest {

    @InjectMocks
    private TextToSpeechController textToSpeechController;

    @Mock
    private TextToSpeechService textToSpeechService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConvertTextToSpeech_success() {
        // Arrange
        TextToSpeechRequest request = new TextToSpeechRequest();
        request.setText("Hello");
        request.setVoiceId("voice123");

        byte[] mockAudioBytes = "mock audio content".getBytes();
        when(textToSpeechService.convertTextToSpeech(any(TextToSpeechRequest.class))).thenReturn(mockAudioBytes);

        // Act
        ResponseEntity<byte[]> response = textToSpeechController.convertTextToSpeech(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockAudioBytes, response.getBody());
    }

    @Test
    void testConvertTextToSpeech_serviceThrowsException() {
        // Arrange
        TextToSpeechRequest request = new TextToSpeechRequest();
        request.setText("Hello");
        request.setVoiceId("voice123");

        when(textToSpeechService.convertTextToSpeech(any(TextToSpeechRequest.class)))
                .thenThrow(new RuntimeException("Service error"));

        // Act
        ResponseEntity<byte[]> response = textToSpeechController.convertTextToSpeech(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
