package com.learning.journalApp.controller;

import com.learning.journalApp.service.TextToSpeechService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tts")
public class TextToSpeechController {

    private static final Logger logger = LoggerFactory.getLogger(TextToSpeechController.class);
    private final TextToSpeechService textToSpeechService;

    public TextToSpeechController(TextToSpeechService textToSpeechService) {
        this.textToSpeechService = textToSpeechService;
    }

    @PostMapping
    public ResponseEntity<byte[]> generateSpeech(@RequestParam String text) {
        logger.info("Received text-to-speech request for text: {}", text);

        byte[] audioBytes;
        try {
            audioBytes = textToSpeechService.convertTextToSpeech(text);
            logger.info("Text-to-speech conversion successful");
        } catch (Exception e) {
            logger.error("Error during text-to-speech conversion", e);
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok()
                .header("Content-Type", "audio/mpeg")
                .header("Content-Disposition", "inline; filename=speech.mp3")
                .body(audioBytes);
    }
}
