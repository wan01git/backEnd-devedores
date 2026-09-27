package com.controlefinanceiro.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// "record" é um recurso do Java (desde a versão 16) para classes que só
// carregam dados imutáveis. Esta linha sozinha gera: construtor, os métodos
// nome(), equals(), hashCode() e toString(). Sem record, isso seriam umas
// 30 linhas de boilerplate para uma classe tão simples.
//
// As anotações de validação (@NotBlank, @Size) só têm efeito quando o
// Controller usar @Valid no parâmetro — isso vem na próxima etapa. Por
// enquanto elas só documentam a regra.
public record PessoaRequest(

        @NotBlank(message = "informe o nome da pessoa")
        @Size(max = 150, message = "o nome pode ter no máximo 150 caracteres")
        String nome
) {
}
