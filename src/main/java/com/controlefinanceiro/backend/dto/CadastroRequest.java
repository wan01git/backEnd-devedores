package com.controlefinanceiro.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastroRequest(

        @NotBlank(message = "informe seu nome")
        @Size(max = 150)
        String nome,

        @NotBlank(message = "informe seu e-mail")
        @Email(message = "e-mail em formato inválido")
        String email,

        @NotBlank(message = "informe uma senha")
        @Size(min = 8, message = "a senha deve ter no mínimo 8 caracteres")
        String senha
) {
}
