package com.controlefinanceiro.backend.service;

import com.controlefinanceiro.backend.dto.LancamentoRequest;
import com.controlefinanceiro.backend.dto.LancamentoResponse;
import com.controlefinanceiro.backend.entity.Lancamento;
import com.controlefinanceiro.backend.entity.Pessoa;
import com.controlefinanceiro.backend.exception.RecursoNaoEncontradoException;
import com.controlefinanceiro.backend.repository.LancamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class LancamentoService {

    private final LancamentoRepository lancamentoRepository;
    private final PessoaService pessoaService;

    public LancamentoService(LancamentoRepository lancamentoRepository, PessoaService pessoaService) {
        this.lancamentoRepository = lancamentoRepository;
        this.pessoaService = pessoaService;
    }

    @Transactional
    public LancamentoResponse criar(Long pessoaId, Long usuarioId, LancamentoRequest request) {
        // Reaproveita a checagem de posse do PessoaService: se a pessoa não
        // existe OU pertence a outro usuário, a exceção é lançada aqui,
        // antes de qualquer lançamento ser criado. O valor > 0 já foi
        // validado pelo @Positive do DTO (checado pelo Controller com
        // @Valid, na próxima etapa) — e reforçado de novo pelo CHECK do
        // banco. Três camadas pegando o mesmo tipo de erro não é
        // redundância inútil: cada uma cobre um jeito diferente de esse
        // dado chegar até aqui (API, um teste, uma migração futura de dados).
        Pessoa pessoa = pessoaService.buscarDoUsuario(pessoaId, usuarioId);

        Lancamento lancamento = new Lancamento(
                pessoa,
                request.tipo(),
                request.valor(),
                request.descricao(),
                request.dataLancamento()
        );

        lancamento = lancamentoRepository.save(lancamento);
        return paraResponse(lancamento);
    }

    @Transactional(readOnly = true)
    public List<LancamentoResponse> listar(Long pessoaId, Long usuarioId, LocalDate inicio, LocalDate fim) {
        pessoaService.buscarDoUsuario(pessoaId, usuarioId); // valida posse antes de listar

        List<Lancamento> lancamentos = (inicio != null && fim != null)
                ? lancamentoRepository.findByPessoaIdAndDataLancamentoBetweenOrderByDataLancamentoDesc(pessoaId, inicio, fim)
                : lancamentoRepository.findByPessoaIdOrderByDataLancamentoDesc(pessoaId);

        return lancamentos.stream().map(this::paraResponse).toList();
    }

    @Transactional
    public void excluir(Long lancamentoId, Long usuarioId) {
        Lancamento lancamento = lancamentoRepository.findByIdAndPessoaUsuarioId(lancamentoId, usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("lançamento não encontrado"));
        lancamentoRepository.delete(lancamento);
    }

    private LancamentoResponse paraResponse(Lancamento l) {
        return new LancamentoResponse(l.getId(), l.getTipo(), l.getValor(), l.getDescricao(), l.getDataLancamento());
    }
}
