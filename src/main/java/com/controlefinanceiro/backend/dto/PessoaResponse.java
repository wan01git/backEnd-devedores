package com.controlefinanceiro.backend.dto;

import java.math.BigDecimal;

// saldo positivo = a pessoa deve para você (credor)
// saldo negativo = você deve para a pessoa (devedor)
public record PessoaResponse(
        Long id,
        String nome,
        BigDecimal saldo
) {
}
