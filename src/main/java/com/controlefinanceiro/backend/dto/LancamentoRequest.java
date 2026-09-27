package com.controlefinanceiro.backend.dto;

import com.controlefinanceiro.backend.entity.TipoLancamento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoRequest(

        @NotNull(message = "informe o tipo (A_PAGAR ou A_RECEBER)")
        TipoLancamento tipo,

        @NotNull(message = "informe o valor")
        @Positive(message = "o valor deve ser maior que zero")
        BigDecimal valor,

        @Size(max = 255, message = "a descrição pode ter no máximo 255 caracteres")
        String descricao,

        // pode vir null: se o cliente não informar, a entidade Lancamento usa
        // LocalDate.now() como padrão (isso está no construtor da entidade)
        LocalDate dataLancamento
) {
}
