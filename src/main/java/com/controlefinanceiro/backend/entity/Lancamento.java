package com.controlefinanceiro.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "lancamentos")
public class Lancamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_id", nullable = false)
    private Pessoa pessoa;

    // EnumType.STRING grava o texto "A_PAGAR"/"A_RECEBER" na coluna (batendo
    // com o CHECK do banco). O padrão do JPA seria EnumType.ORDINAL, que grava
    // 0/1 — nunca use ORDINAL: se um dia você reordenar o enum, os dados
    // antigos passam a apontar para o valor errado silenciosamente.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoLancamento tipo;

    // BigDecimal, nunca double/float, para dinheiro. double é ponto flutuante
    // binário: 0.1 + 0.2 em double não dá exatamente 0.3. Para valores
    // financeiros isso pode gerar diferenças de centavos que se acumulam.
    // precision/scale aqui espelham o NUMERIC(12,2) do banco.
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Column(length = 255)
    private String descricao;

    @Column(name = "data_lancamento", nullable = false)
    private LocalDate dataLancamento;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    protected Lancamento() {
    }

    public Lancamento(Pessoa pessoa, TipoLancamento tipo, BigDecimal valor, String descricao, LocalDate dataLancamento) {
        this.pessoa = pessoa;
        this.tipo = tipo;
        this.valor = valor;
        this.descricao = descricao;
        this.dataLancamento = dataLancamento != null ? dataLancamento : LocalDate.now();
    }

    @PrePersist
    protected void aoPersistir() {
        this.criadoEm = OffsetDateTime.now();
    }

    public Long getId() { return id; }
    public Pessoa getPessoa() { return pessoa; }
    public TipoLancamento getTipo() { return tipo; }
    public BigDecimal getValor() { return valor; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDate getDataLancamento() { return dataLancamento; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }

    // Propositalmente NÃO existe setValor/setTipo/setPessoa: um lançamento
    // financeiro, no nosso modelo, não deve ser "editado" no valor ou tipo
    // depois de criado — se foi um erro, o fluxo correto é excluir e lançar
    // de novo. Isso evita um histórico de alterações silenciosas em valores
    // que compõem o saldo. (Podemos revisar essa regra mais pra frente se
    // você preferir permitir edição.)

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lancamento other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
