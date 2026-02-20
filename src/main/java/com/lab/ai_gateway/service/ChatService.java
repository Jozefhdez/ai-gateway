package com.lab.ai_gateway.service;

import com.lab.ai_gateway.client.OllamaClient;
import com.lab.ai_gateway.model.ChatRequest;
import com.lab.ai_gateway.model.ChatResponse;
import com.lab.ai_gateway.model.OllamaRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ChatService {

    private final OllamaClient ollamaClient;
    private final AtomicInteger requestCounter = new AtomicInteger(0); // Contador atómico

    @Value("${ai.engine.model}")
    private String defaultModel;

    public ChatService(OllamaClient ollamaClient) {
        this.ollamaClient = ollamaClient;
    }

    // Método para resetear el contador cada minuto
    @Scheduled(fixedRate = 60000)
    public void resetCounter() {
        requestCounter.set(0);
    }

    public Mono<ChatResponse> chat(ChatRequest request) {
        // Verificar limite
        if (requestCounter.incrementAndGet() > 5) {
            return Mono.error(new RateLimitException("Too many requests. Try again in a minute."));
        }

        long start = System.currentTimeMillis();
        String model = (request.model() != null && !request.model().isBlank()) ? request.model() : defaultModel;
        OllamaRequest ollamaRequest = new OllamaRequest(model, request.prompt(), false);

        return ollamaClient.generate(ollamaRequest)
                .map(responseText -> {
                    long durationMs = System.currentTimeMillis() - start;
                    return ChatResponse.of(responseText, model, durationMs);
                })
                .onErrorMap(ex -> {
                    if (ex instanceof RateLimitException) return ex;
                    return new ServiceUnavailableException("AI Engine is unreachable: " + ex.getMessage());
                });
    }
}

class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }
}