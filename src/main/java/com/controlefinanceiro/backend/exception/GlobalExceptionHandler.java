package com.controlefinanceiro.backend.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

// @RestControllerAdvice funciona como um "catch" global: qualquer Controller
// da aplicação que deixar uma dessas exceções escapar tem a exceção
// interceptada aqui, e transformada numa resposta HTTP apropriada — em vez
// de virar um genérico "500 Internal Server Error" sem explicação nenhuma
// para quem está chamando a API (o Angular, no seu caso).
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> tratar(RecursoNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<Map<String, String>> tratar(EmailJaCadastradoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("erro", ex.getMessage()));
    }

    // Pega qualquer violação de constraint do banco que não tratamos
    // explicitamente antes — o caso mais comum aqui é o UNIQUE
    // (usuario_id, nome) da tabela pessoas: duas pessoas com o mesmo nome
    // para o mesmo usuário. Sem este handler, isso vazava como 500.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> tratar(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("erro", "já existe um registro com esses dados (ex: nome de pessoa duplicado)"));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> tratar(BadCredentialsException ex) {
        // Mensagem propositalmente genérica: nunca diga "e-mail não existe"
        // vs "senha errada" separadamente — isso permite que alguém
        // descubra quais e-mails estão cadastrados só tentando logar.
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", "e-mail ou senha inválidos"));
    }

    // Disparada quando o @Valid de um Controller encontra campos inválidos
    // no corpo da requisição. Em vez de devolver uma mensagem genérica,
    // montamos um mapa campo -> mensagem, para o Angular conseguir mostrar
    // o erro embaixo do campo certo no formulário.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> tratar(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(erro ->
                erros.put(erro.getField(), erro.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros);
    }
}
