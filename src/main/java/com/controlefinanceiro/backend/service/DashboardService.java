package com.controlefinanceiro.backend.service;

import com.controlefinanceiro.backend.dto.DashboardResponse;
import com.controlefinanceiro.backend.dto.PessoaResponse;
import com.controlefinanceiro.backend.repository.LancamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DashboardService {

    private final LancamentoRepository lancamentoRepository;
    private final PessoaService pessoaService;

    public DashboardService(LancamentoRepository lancamentoRepository, PessoaService pessoaService) {
        this.lancamentoRepository = lancamentoRepository;
        this.pessoaService = pessoaService;
    }

    @Transactional(readOnly = true)
    public DashboardResponse obter(Long usuarioId) {
        BigDecimal saldoGeral = lancamentoRepository.calcularSaldoGeral(usuarioId);
        BigDecimal totalAReceber = lancamentoRepository.calcularTotalAReceber(usuarioId);
        BigDecimal totalAPagar = lancamentoRepository.calcularTotalAPagar(usuarioId);
        List<PessoaResponse> pessoas = pessoaService.listar(usuarioId);

        return new DashboardResponse(saldoGeral, totalAReceber, totalAPagar, pessoas);
    }
}
