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

    // TODO A: Inyección por constructor
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // TODO B: Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("{ \"status\": \"UP\" }");
    }

    // TODO C: El endpoint principal POST
    @PostMapping
    public Mono<ResponseEntity<ChatResponse>> sendMessage(@Valid @RequestBody ChatRequest request) {
        return chatService.chat(request)
                .map(ResponseEntity::ok)
                .onErrorResume(ex -> Mono.just(
                        ResponseEntity.status(503).build()
                ));
    }
}