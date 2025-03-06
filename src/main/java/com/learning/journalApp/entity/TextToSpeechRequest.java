package com.learning.journalApp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class TextToSpeechRequest {

    @JsonProperty("text")
    private String text;

    @JsonProperty("model_id")
    private String modelId;

    public TextToSpeechRequest(String text, String modelId) {
        this.text = text;
        this.modelId = modelId;
    }

}
