package com.controlefinanceiro.backend.controller;

import com.controlefinanceiro.backend.dto.LancamentoRequest;
import com.controlefinanceiro.backend.dto.LancamentoResponse;
import com.controlefinanceiro.backend.security.UsuarioAutenticado;
import com.controlefinanceiro.backend.service.LancamentoService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

// A rota é aninhada sob /pessoas/{pessoaId}/lancamentos porque um lançamento
// só faz sentido no contexto de uma pessoa — isso deixa a API mais legível
// (GET /pessoas/7/lancamentos já diz "os lançamentos DA pessoa 7") e evita
// que o cliente esqueça de informar de quem são os lançamentos.
@RestController
@RequestMapping("/pessoas/{pessoaId}/lancamentos")
public class LancamentoController {

    private final LancamentoService lancamentoService;

    public LancamentoController(LancamentoService lancamentoService) {
        this.lancamentoService = lancamentoService;
    }

    @PostMapping
    public ResponseEntity<LancamentoResponse> criar(@PathVariable Long pessoaId,
                                                      @AuthenticationPrincipal UsuarioAutenticado usuario,
                                                      @Valid @RequestBody LancamentoRequest request) {
        LancamentoResponse response = lancamentoService.criar(pessoaId, usuario.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Suporta o requisito de "detalhamento por período": sem os parâmetros
    // inicio/fim, lista tudo; com eles, filtra. @DateTimeFormat ensina o
    // Spring a converter "2026-01-01" (string da URL) em LocalDate.
    // Exemplo: GET /pessoas/7/lancamentos?inicio=2026-01-01&fim=2026-01-31
    @GetMapping
    public ResponseEntity<List<LancamentoResponse>> listar(
            @PathVariable Long pessoaId,
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(lancamentoService.listar(pessoaId, usuario.getId(), inicio, fim));
    }

    @DeleteMapping("/{lancamentoId}")
    public ResponseEntity<Void> excluir(@PathVariable Long pessoaId,
                                         @PathVariable Long lancamentoId,
                                         @AuthenticationPrincipal UsuarioAutenticado usuario) {
        // pessoaId no path é só para a URL ficar legível; a segurança de
        // verdade vem do usuario.getId() checado dentro do Service.
        lancamentoService.excluir(lancamentoId, usuario.getId());
        return ResponseEntity.noContent().build();
    }
}
