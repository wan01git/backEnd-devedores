package com.controlefinanceiro.backend.repository;

import com.controlefinanceiro.backend.entity.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

    // Lista as pessoas de UM usuário específico. No dashboard e na tela de
    // listagem, é essa consulta que vamos usar — nunca findAll().
    List<Pessoa> findByUsuarioIdOrderByNomeAsc(Long usuarioId);

    // Este é o método mais importante do arquivo, do ponto de vista de
    // segurança. Numa aplicação multiusuário, buscar só "findById(id)" é
    // perigoso: qualquer usuário logado poderia trocar o id na URL
    // (ex: GET /pessoas/57) e ler ou editar uma pessoa que pertence a OUTRO
    // usuário — essa falha se chama "Insecure Direct Object Reference" (IDOR),
    // está no OWASP Top 10 de vulnerabilidades web.
    //
    // Por isso toda busca de uma Pessoa específica no Service vai usar este
    // método, passando também o id do usuário autenticado. Se a pessoa existe
    // mas pertence a outro usuário, retorna vazio — como se não existisse.
    Optional<Pessoa> findByIdAndUsuarioId(Long id, Long usuarioId);
}
