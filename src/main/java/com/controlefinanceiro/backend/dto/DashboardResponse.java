package com.controlefinanceiro.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
        BigDecimal saldoGeral,
        BigDecimal totalAReceber,
        BigDecimal totalAPagar,
        List<PessoaResponse> pessoas
) {
}
