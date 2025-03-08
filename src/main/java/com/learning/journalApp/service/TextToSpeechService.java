package com.learning.journalApp.service;

import com.learning.journalApp.cache.AppCache;
import com.learning.journalApp.client.TextToSpeechClient;
import com.learning.journalApp.entity.TextToSpeechRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TextToSpeechService {

    private static final Logger logger = LoggerFactory.getLogger(TextToSpeechService.class);
    private final TextToSpeechClient textToSpeechClient;

    private final AppCache appCache;

    public TextToSpeechService(TextToSpeechClient textToSpeechClient, AppCache appCache) {
        this.textToSpeechClient = textToSpeechClient;
        this.appCache = appCache;
    }

    public void convertTextToSpeech(String text) {
        logger.debug("Preparing request to Eleven Labs API");

        try {
            TextToSpeechRequest request = new TextToSpeechRequest(text, "eleven_multilingual_v2");
            String ttsApiKey = appCache.getConfigValue(String.valueOf(AppCache.keys.TTS_API_KEY));
            textToSpeechClient.generateSpeech(ttsApiKey, request);
            logger.info("Successfully generated speech for text: {}", text);
        } catch (Exception e) {
            logger.error("Error calling Eleven Labs API", e);
            throw new RuntimeException("Failed to generate speech", e);
        }
    }
}
