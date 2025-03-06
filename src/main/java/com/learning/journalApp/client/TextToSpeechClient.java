package com.learning.journalApp.client;

import com.learning.journalApp.entity.TextToSpeechRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "textToSpeechClient", url = "https://api.elevenlabs.io/v1")
public interface TextToSpeechClient {

    Logger logger = LoggerFactory.getLogger(TextToSpeechClient.class);

    @PostMapping(value = "/text-to-speech/TX3LPaxmHKxFdv7VOQHJ?output_format=mp3_44100_128&enable_logging=true",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    byte[] generateSpeech(
            @RequestHeader("xi-api-key") String apiKey,
            @RequestBody TextToSpeechRequest requestBody
    );

    default byte[] logRequest(String apiKey, TextToSpeechRequest requestBody) {
        logger.debug("Sending request to Eleven Labs API: {}", requestBody);
        return generateSpeech(apiKey, requestBody);
    }
}
