package com.controlefinanceiro.backend.repository;

import com.controlefinanceiro.backend.entity.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    // Detalhamento de uma pessoa: todos os lançamentos, mais recentes primeiro.
    List<Lancamento> findByPessoaIdOrderByDataLancamentoDesc(Long pessoaId);

    // Mesmo detalhamento, mas filtrado por período — é a query que alimenta
    // o requisito de "ver os lançamentos feitos num período". Repare que o
    // nome do método descreve a query inteira: Between já vira um
    // "WHERE data_lancamento BETWEEN ? AND ?".
    List<Lancamento> findByPessoaIdAndDataLancamentoBetweenOrderByDataLancamentoDesc(
            Long pessoaId, LocalDate inicio, LocalDate fim);

    // Mesma ideia do findByIdAndUsuarioId do PessoaRepository, mas aqui a
    // "cadeia de posse" tem um passo a mais: Lancamento -> Pessoa -> Usuario.
    // O Spring Data entende esse caminho pelo nome do método
    // (Pessoa.Usuario.Id) e monta o JOIN sozinho. Usado antes de excluir um
    // lançamento, para confirmar que ele pertence ao usuário logado.
    Optional<Lancamento> findByIdAndPessoaUsuarioId(Long id, Long usuarioId);

    // A partir daqui não dá mais para descrever a query só pelo nome do
    // método — usamos JPQL (parecido com SQL, mas opera sobre entidades e
    // atributos Java, não sobre tabelas e colunas; por isso é "Lancamento l"
    // e "l.valor", não "lancamentos" e "valor").
    //
    // COALESCE(..., 0) é necessário porque SUM sobre um conjunto vazio
    // retorna NULL — se uma pessoa não tem nenhum lançamento ainda, sem o
    // COALESCE o saldo viria como null em vez de zero.
    @Query("""
            SELECT COALESCE(SUM(
                CASE WHEN l.tipo = com.controlefinanceiro.backend.entity.TipoLancamento.A_RECEBER
                     THEN l.valor ELSE -l.valor END
            ), 0)
            FROM Lancamento l
            WHERE l.pessoa.id = :pessoaId
            """)
    BigDecimal calcularSaldoPorPessoa(@Param("pessoaId") Long pessoaId);

    // Mesma lógica, mas somando TODOS os lançamentos de TODAS as pessoas de
    // um usuário — é o número principal do dashboard (saldo geral).
    @Query("""
            SELECT COALESCE(SUM(
                CASE WHEN l.tipo = com.controlefinanceiro.backend.entity.TipoLancamento.A_RECEBER
                     THEN l.valor ELSE -l.valor END
            ), 0)
            FROM Lancamento l
            WHERE l.pessoa.usuario.id = :usuarioId
            """)
    BigDecimal calcularSaldoGeral(@Param("usuarioId") Long usuarioId);

    // Totais separados (quanto tenho a receber no total / quanto tenho a
    // pagar no total) — úteis para dois cards separados no dashboard, além
    // do saldo líquido.
    @Query("""
            SELECT COALESCE(SUM(l.valor), 0)
            FROM Lancamento l
            WHERE l.pessoa.usuario.id = :usuarioId AND l.tipo = com.controlefinanceiro.backend.entity.TipoLancamento.A_RECEBER
            """)
    BigDecimal calcularTotalAReceber(@Param("usuarioId") Long usuarioId);

    @Query("""
            SELECT COALESCE(SUM(l.valor), 0)
            FROM Lancamento l
            WHERE l.pessoa.usuario.id = :usuarioId AND l.tipo = com.controlefinanceiro.backend.entity.TipoLancamento.A_PAGAR
            """)
    BigDecimal calcularTotalAPagar(@Param("usuarioId") Long usuarioId);
}
