package com.learning.journalApp.service;

import com.learning.journalApp.client.TextToSpeechClient;
import com.learning.journalApp.entity.TextToSpeechRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TextToSpeechService {

    private static final Logger logger = LoggerFactory.getLogger(TextToSpeechService.class);
    private final TextToSpeechClient textToSpeechClient;

    @Value("${tts.api.key}")
    private String ttsApiKey;

    public TextToSpeechService(TextToSpeechClient textToSpeechClient) {
        this.textToSpeechClient = textToSpeechClient;
    }

    public byte[] convertTextToSpeech(String text) {
        logger.debug("Preparing request to Eleven Labs API");

        try {
            TextToSpeechRequest request = new TextToSpeechRequest(text, "eleven_multilingual_v2");
            byte[] response = textToSpeechClient.generateSpeech(ttsApiKey, request);
            logger.info("Successfully generated speech for text: {}", text);
            return response;
        } catch (Exception e) {
            logger.error("Error calling Eleven Labs API", e);
            throw new RuntimeException("Failed to generate speech", e);
        }
    }
}
