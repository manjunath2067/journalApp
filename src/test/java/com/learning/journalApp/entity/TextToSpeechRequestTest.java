package com.learning.journalApp.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TextToSpeechRequestTest {

    @Test
    void testNoArgsConstructor() {
        TextToSpeechRequest request = new TextToSpeechRequest();
        assertNull(request.getText());
        assertNull(request.getVoiceId());
    }

    @Test
    void testGettersAndSetters() {
        // Arrange
        TextToSpeechRequest request = new TextToSpeechRequest();
        String text = "Hello world";
        String voiceId = "en-US-Wavenet-F";

        // Act
        request.setText(text);
        request.setVoiceId(voiceId);

        // Assert
        assertEquals(text, request.getText());
        assertEquals(voiceId, request.getVoiceId());
    }
}
