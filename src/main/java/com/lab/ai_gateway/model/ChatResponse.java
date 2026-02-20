package com.lab.ai_gateway.model;

import java.time.Instant;

public record ChatResponse(
        String response,
        String model,
        long durationMs,
        Instant timestamp
) {
    public static ChatResponse of(String response, String model, long durationMs) {
        return new ChatResponse(response, model, durationMs, Instant.now());
    }
}