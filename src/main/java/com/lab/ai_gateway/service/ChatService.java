package com.lab.ai_gateway.service;

import com.lab.ai_gateway.client.OllamaClient;
import com.lab.ai_gateway.model.ChatRequest;
import com.lab.ai_gateway.model.ChatResponse;
import com.lab.ai_gateway.model.OllamaRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ChatService {

    private final OllamaClient ollamaClient;

    @Value("${ai.engine.model}")
    private String defaultModel;

    public ChatService(OllamaClient ollamaClient) {
        this.ollamaClient = ollamaClient;
    }

    public Mono<ChatResponse> chat(ChatRequest request) {
        long start = System.currentTimeMillis();

        String model = (request.model() != null && !request.model().isBlank())
                ? request.model()
                : defaultModel;

        OllamaRequest ollamaRequest = new OllamaRequest(model, request.prompt(), false);

        return ollamaClient.generate(ollamaRequest)
                .map(responseText -> {
                    long durationMs = System.currentTimeMillis() - start;
                    return ChatResponse.of(responseText, model, durationMs);
                })
                .onErrorMap(ex -> new ServiceUnavailableException("AI Engine is unreachable: " + ex.getMessage()));
    }
}

class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }
}