package com.controlefinanceiro.backend.service;

import com.controlefinanceiro.backend.dto.PessoaRequest;
import com.controlefinanceiro.backend.dto.PessoaResponse;
import com.controlefinanceiro.backend.entity.Pessoa;
import com.controlefinanceiro.backend.entity.Usuario;
import com.controlefinanceiro.backend.exception.RecursoNaoEncontradoException;
import com.controlefinanceiro.backend.repository.LancamentoRepository;
import com.controlefinanceiro.backend.repository.PessoaRepository;
import com.controlefinanceiro.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

// @Service marca esta classe como um "bean" gerenciado pelo Spring — ele
// cria UMA instância dela (singleton, por padrão) na inicialização e injeta
// essa mesma instância em quem precisar (ex: nos Controllers, na próxima
// etapa).
@Service
public class PessoaService {

    private final PessoaRepository pessoaRepository;
    private final UsuarioRepository usuarioRepository;
    private final LancamentoRepository lancamentoRepository;

    // Injeção via construtor: o Spring olha os parâmetros deste construtor,
    // percebe que são outros beans que ele já gerencia (os 3 repositories) e
    // preenche sozinho. É preferível a anotar campos com @Autowired porque:
    // os campos podem ser "final" (imutáveis após a construção), fica
    // explícito quais dependências a classe tem, e em testes você consegue
    // instanciar PessoaService passando mocks direto, sem precisar subir o
    // contexto do Spring.
    public PessoaService(PessoaRepository pessoaRepository,
                          UsuarioRepository usuarioRepository,
                          LancamentoRepository lancamentoRepository) {
        this.pessoaRepository = pessoaRepository;
        this.usuarioRepository = usuarioRepository;
        this.lancamentoRepository = lancamentoRepository;
    }

    // @Transactional abre uma transação de banco antes do método rodar e
    // faz commit no final (ou rollback automático se alguma RuntimeException
    // escapar do método). Aqui isso importa pouco (é só um INSERT), mas em
    // métodos que fazem múltiplas operações é o que garante que tudo aconteça
    // "tudo ou nada".
    @Transactional
    public PessoaResponse criar(Long usuarioId, PessoaRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("usuário não encontrado"));

        Pessoa pessoa = new Pessoa(usuario, request.nome());
        pessoa = pessoaRepository.save(pessoa);

        // pessoa recém-criada nunca tem lançamento ainda, então o saldo é
        // sempre zero aqui — não precisa consultar o banco de novo.
        return new PessoaResponse(pessoa.getId(), pessoa.getNome(), BigDecimal.ZERO);
    }

    // readOnly = true é uma dica para o Hibernate: ele pode pular algumas
    // verificações internas (como o "dirty checking", que detecta mudanças
    // em entidades para decidir o que fazer UPDATE) porque sabe que nada
    // será alterado neste método. Pequeno ganho de performance, boa prática
    // em todo método que só lê dados.
    @Transactional(readOnly = true)
    public List<PessoaResponse> listar(Long usuarioId) {
        return pessoaRepository.findByUsuarioIdOrderByNomeAsc(usuarioId).stream()
                .map(pessoa -> new PessoaResponse(
                        pessoa.getId(),
                        pessoa.getNome(),
                        lancamentoRepository.calcularSaldoPorPessoa(pessoa.getId())
                ))
                .toList();
    }

    // Usado pelos outros Services (Lancamento, Dashboard) sempre que
    // precisam confirmar que uma Pessoa pertence ao usuário autenticado
    // antes de operar sobre ela. Centralizar essa checagem aqui evita
    // repetir a mesma lógica de segurança em vários lugares.
    @Transactional(readOnly = true)
    public Pessoa buscarDoUsuario(Long pessoaId, Long usuarioId) {
        return pessoaRepository.findByIdAndUsuarioId(pessoaId, usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("pessoa não encontrada"));
    }

    // Versão "pronta para a API": já embrulha em PessoaResponse com o saldo
    // calculado, para o Controller de detalhe de uma pessoa usar direto.
    @Transactional(readOnly = true)
    public PessoaResponse buscarResponse(Long pessoaId, Long usuarioId) {
        Pessoa pessoa = buscarDoUsuario(pessoaId, usuarioId);
        BigDecimal saldo = lancamentoRepository.calcularSaldoPorPessoa(pessoa.getId());
        return new PessoaResponse(pessoa.getId(), pessoa.getNome(), saldo);
    }

    @Transactional
    public void excluir(Long pessoaId, Long usuarioId) {
        Pessoa pessoa = buscarDoUsuario(pessoaId, usuarioId);
        // cascade = ALL + orphanRemoval na entidade Pessoa (e o ON DELETE
        // CASCADE no banco) garantem que os lançamentos dessa pessoa somem
        // junto — não precisa apagar um por um manualmente aqui.
        pessoaRepository.delete(pessoa);
    }
}
