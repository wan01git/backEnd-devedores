package com.controlefinanceiro.backend.controller;

import com.controlefinanceiro.backend.dto.PessoaRequest;
import com.controlefinanceiro.backend.dto.PessoaResponse;
import com.controlefinanceiro.backend.security.UsuarioAutenticado;
import com.controlefinanceiro.backend.service.PessoaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {

    private final PessoaService pessoaService;

    public PessoaController(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    // @AuthenticationPrincipal é a peça que fecha o ciclo iniciado no
    // JwtAuthenticationFilter: o filtro colocou um UsuarioAutenticado no
    // SecurityContext, e esta anotação instrui o Spring a extrair esse
    // objeto e injetar direto no parâmetro do método — sem você escrever
    // nenhum código manual para "descobrir quem está logado".
    @PostMapping
    public ResponseEntity<PessoaResponse> criar(@AuthenticationPrincipal UsuarioAutenticado usuario,
                                                 @Valid @RequestBody PessoaRequest request) {
        PessoaResponse response = pessoaService.criar(usuario.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PessoaResponse>> listar(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(pessoaService.listar(usuario.getId()));
    }

    @GetMapping("/{pessoaId}")
    public ResponseEntity<PessoaResponse> buscar(@PathVariable Long pessoaId,
                                                  @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(pessoaService.buscarResponse(pessoaId, usuario.getId()));
    }

    @DeleteMapping("/{pessoaId}")
    public ResponseEntity<Void> excluir(@PathVariable Long pessoaId,
                                         @AuthenticationPrincipal UsuarioAutenticado usuario) {
        pessoaService.excluir(pessoaId, usuario.getId());
        return ResponseEntity.noContent().build();
    }
}
