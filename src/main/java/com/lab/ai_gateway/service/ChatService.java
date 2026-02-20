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

    // TODO 1: Inyección por constructor
    public ChatService(OllamaClient ollamaClient) {
        this.ollamaClient = ollamaClient;
    }

    /**
     * Procesa la petición y devuelve la respuesta de la IA.
     */
    public Mono<ChatResponse> chat(ChatRequest request) {
        long start = System.currentTimeMillis();

        // TODO 2: Lógica para elegir el modelo
        // Si el usuario no manda un modelo, usamos el default del application.properties
        String model = (request.model() != null && !request.model().isBlank())
                ? request.model()
                : defaultModel;

        // TODO 3: Crear el OllamaRequest
        // Ponemos stream en false para que sea una respuesta única y no por pedazos
        OllamaRequest ollamaRequest = new OllamaRequest(model, request.prompt(), false);

        // TODO 4: Llamar al cliente y transformar la respuesta
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