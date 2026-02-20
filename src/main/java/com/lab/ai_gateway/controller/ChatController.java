package com.lab.ai_gateway.controller;

import com.lab.ai_gateway.model.ChatRequest;
import com.lab.ai_gateway.model.ChatResponse;
import com.lab.ai_gateway.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("{ \"status\": \"UP\" }");
    }

    @GetMapping("/models")
    public ResponseEntity<String[]> getModels() {
        String[] models = {"llama3", "mistral", "phi3"};
        return ResponseEntity.ok(models);
    }

    @PostMapping
    public Mono<ResponseEntity<ChatResponse>> sendMessage(@Valid @RequestBody ChatRequest request) {
        return chatService.chat(request)
                .map(ResponseEntity::ok)
                .onErrorResume(ex -> Mono.just(
                        ResponseEntity.status(503).build()
                ));
    }
}