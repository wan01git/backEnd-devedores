package com.controlefinanceiro.backend.dto;

import com.controlefinanceiro.backend.entity.TipoLancamento;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoResponse(
        Long id,
        TipoLancamento tipo,
        BigDecimal valor,
        String descricao,
        LocalDate dataLancamento
) {
}
