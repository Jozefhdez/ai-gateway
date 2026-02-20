package com.lab.ai_gateway.model;

import jakarta. validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(
        @NotBlank(message = "prompt must not be blank")
        @Size(max = 4000, message = "prompt must not exceed 4000 characters")
        String prompt,

        // Opcional: para cambiar el modelo en la petición
        String model
) {}