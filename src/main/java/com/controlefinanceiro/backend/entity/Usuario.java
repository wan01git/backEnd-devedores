package com.controlefinanceiro.backend.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // espelha o BIGSERIAL: quem gera o valor é o Postgres, não o Java
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "senha_hash", nullable = false, length = 255) // nome da coluna é diferente do nome do atributo Java
    private String senhaHash;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    // mappedBy = "usuario" aponta para o atributo "usuario" lá na classe Pessoa.
    // Isso diz ao Hibernate: "quem é dono da relação é a Pessoa (ela tem a FK)".
    // Usuario aqui só enxerga a lista, mas não guarda coluna nenhuma por causa disso.
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pessoa> pessoas = new ArrayList<>();

    // Construtor vazio: o Hibernate precisa dele para criar o objeto via reflection
    // antes de preencher os campos com os dados vindos do banco. "protected" evita
    // que outra classe do sistema use esse construtor por engano e crie um Usuario
    // sem nome/email.
    protected Usuario() {
    }

    // Construtor "de negócio": obriga quem cria um Usuario a fornecer o que é
    // obrigatório. Isso é uma forma de impedir estados inválidos no seu domínio.
    public Usuario(String nome, String email, String senhaHash) {
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
    }

    // O banco tem "DEFAULT now()" na coluna criado_em, mas esse default só é
    // aplicado quando a coluna é OMITIDA do INSERT. O Hibernate sempre envia
    // todas as colunas mapeadas — se criadoEm estiver null em Java, ele manda
    // um NULL explícito e o DEFAULT do banco nunca entra em ação, quebrando o
    // NOT NULL. Por isso preenchemos aqui, do lado da aplicação.
    @PrePersist
    protected void aoPersistir() {
        this.criadoEm = OffsetDateTime.now();
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public List<Pessoa> getPessoas() { return pessoas; }

    // equals/hashCode baseados só no id (e não em todos os campos) é a prática
    // recomendada em entidades JPA. Se usássemos todos os campos, dois objetos
    // "iguais" poderiam parar de ser iguais assim que um setter mudasse um
    // valor — o que quebra o contrato de equals para objetos guardados em Set,
    // por exemplo. E antes do primeiro save() o id é null, então dois objetos
    // "novos" nunca são considerados iguais entre si.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
