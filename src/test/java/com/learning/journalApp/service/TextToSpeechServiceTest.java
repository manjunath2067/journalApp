package com.learning.journalApp.service;

import com.learning.journalApp.client.TextToSpeechClient;
import com.learning.journalApp.entity.TextToSpeechRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TextToSpeechServiceTest {

    @Mock
    private TextToSpeechClient textToSpeechClient;

    @InjectMocks
    private TextToSpeechService textToSpeechService;

    private TextToSpeechRequest request;
    private byte[] audioContent;

    @BeforeEach
    void setUp() {
        request = new TextToSpeechRequest();
        request.setText("Hello, this is a test.");
        audioContent = "sample audio".getBytes();
    }

    @Test
    void testConvertTextToSpeech_Success() {
        // Arrange
        ResponseEntity<byte[]> mockResponse = new ResponseEntity<>(audioContent, HttpStatus.OK);
        when(textToSpeechClient.convertTextToSpeech(request.getText())).thenReturn(mockResponse);

        // Act
        ResponseEntity<byte[]> response = textToSpeechService.convertTextToSpeech(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(audioContent, response.getBody());
        verify(textToSpeechClient, times(1)).convertTextToSpeech(request.getText());
    }

    @Test
    void testConvertTextToSpeech_ClientError() {
        // Arrange
        when(textToSpeechClient.convertTextToSpeech(request.getText())).thenThrow(new RuntimeException("API error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            textToSpeechService.convertTextToSpeech(request);
        });
        assertEquals("API error", exception.getMessage());
        verify(textToSpeechClient, times(1)).convertTextToSpeech(request.getText());
    }

    @Test
    void testConvertTextToSpeech_EmptyText() {
        // Arrange
        request.setText("");

        // Act
        ResponseEntity<byte[]> response = textToSpeechService.convertTextToSpeech(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(textToSpeechClient, never()).convertTextToSpeech(anyString());
    }

    @Test
    void testConvertTextToSpeech_NullText() {
        // Arrange
        request.setText(null);

        // Act
        ResponseEntity<byte[]> response = textToSpeechService.convertTextToSpeech(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(textToSpeechClient, never()).convertTextToSpeech(anyString());
    }
}