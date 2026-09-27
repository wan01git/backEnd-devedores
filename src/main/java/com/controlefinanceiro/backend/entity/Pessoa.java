package com.controlefinanceiro.backend.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pessoas")
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Este é o lado "dono" da relação com Usuario: é aqui que mora a coluna
    // usuario_id (a FK de verdade no banco). @JoinColumn diz qual coluna usar.
    // FetchType.LAZY = o Hibernate NÃO traz o Usuario junto quando você busca
    // uma Pessoa; ele só busca o usuário se você chamar getUsuario().algumaCoisa().
    // Isso evita carregar dados que você não vai usar. O padrão do @ManyToOne
    // é EAGER (carrega sempre) — LAZY é quase sempre a escolha certa.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lancamento> lancamentos = new ArrayList<>();

    protected Pessoa() {
    }

    public Pessoa(Usuario usuario, String nome) {
        this.usuario = usuario;
        this.nome = nome;
    }

    @PrePersist
    protected void aoPersistir() {
        this.criadoEm = OffsetDateTime.now();
    }

    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public List<Lancamento> getLancamentos() { return lancamentos; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pessoa other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
