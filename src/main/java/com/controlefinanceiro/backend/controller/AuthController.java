package com.controlefinanceiro.backend.controller;

import com.controlefinanceiro.backend.dto.CadastroRequest;
import com.controlefinanceiro.backend.dto.LoginRequest;
import com.controlefinanceiro.backend.dto.TokenResponse;
import com.controlefinanceiro.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// @RestController = @Controller + @ResponseBody: cada método já devolve o
// corpo da resposta diretamente (convertido para JSON automaticamente pelo
// Jackson, que já vem com o starter-web), em vez de devolver o nome de uma
// página HTML.
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<TokenResponse> cadastrar(@Valid @RequestBody CadastroRequest request) {
        // @Valid dispara as anotações do CadastroRequest (@NotBlank, @Email,
        // @Size). Se algo falhar, o Spring lança MethodArgumentNotValidException
        // ANTES do corpo do método rodar — quem trata isso é o
        // GlobalExceptionHandler, na próxima classe.
        TokenResponse token = authService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
