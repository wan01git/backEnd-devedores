package com.controlefinanceiro.backend.repository;

import com.controlefinanceiro.backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Ao estender JpaRepository<Usuario, Long>, esta interface já ganha de graça:
// save(), findById(), findAll(), deleteById(), count() etc. Você não implementa
// nada disso — o Spring cria a implementação em tempo de execução (um "proxy").
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // O Spring Data lê o NOME do método e monta a query sozinho:
    // "findBy" + "Email" => SELECT * FROM usuarios WHERE email = ?
    // Vamos usar isso no login: dado o e-mail digitado, buscar o usuário
    // para conferir a senha.
    Optional<Usuario> findByEmail(String email);

    // Útil na tela de cadastro, para impedir e-mail duplicado antes de tentar
    // salvar (e receber o erro de UNIQUE constraint vindo do banco).
    boolean existsByEmail(String email);
}
