package com.lab.ai_gateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OllamaRequest(
        String model,
        String prompt,
        @JsonProperty("stream") boolean stream
) {}