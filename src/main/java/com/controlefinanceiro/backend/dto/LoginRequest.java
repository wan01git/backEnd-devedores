package com.controlefinanceiro.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "informe o e-mail")
        String email,

        @NotBlank(message = "informe a senha")
        String senha
) {
}
