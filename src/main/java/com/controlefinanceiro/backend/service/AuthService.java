package com.controlefinanceiro.backend.service;

import com.controlefinanceiro.backend.dto.CadastroRequest;
import com.controlefinanceiro.backend.dto.LoginRequest;
import com.controlefinanceiro.backend.dto.TokenResponse;
import com.controlefinanceiro.backend.entity.Usuario;
import com.controlefinanceiro.backend.exception.EmailJaCadastradoException;
import com.controlefinanceiro.backend.repository.UsuarioRepository;
import com.controlefinanceiro.backend.security.JwtService;
import com.controlefinanceiro.backend.security.UsuarioAutenticado;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository,
                        PasswordEncoder passwordEncoder,
                        AuthenticationManager authenticationManager,
                        JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public TokenResponse cadastrar(CadastroRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailJaCadastradoException("já existe uma conta com este e-mail");
        }

        // Nunca guardamos request.senha() diretamente — só o hash. Se o
        // banco vazar um dia, ninguém recupera a senha original a partir
        // do hash (BCrypt é uma função de mão única).
        String senhaHash = passwordEncoder.encode(request.senha());
        Usuario usuario = new Usuario(request.nome(), request.email(), senhaHash);
        usuario = usuarioRepository.save(usuario);

        // Já devolve um token, para o usuário não precisar fazer login logo
        // depois de se cadastrar.
        String token = jwtService.gerarToken(new UsuarioAutenticado(usuario));
        return new TokenResponse(token);
    }

    public TokenResponse login(LoginRequest request) {
        // authenticationManager.authenticate(...) faz o trabalho pesado:
        // chama o AutenticacaoUsuarioService para buscar o usuário pelo
        // e-mail, usa o PasswordEncoder para comparar a senha digitada com
        // o hash salvo, e lança BadCredentialsException se algo não bater.
        // Repare que não escrevemos essa comparação manualmente em lugar
        // nenhum — é o Spring Security que orquestra isso.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha())
        );

        UsuarioAutenticado usuario = (UsuarioAutenticado) authentication.getPrincipal();
        String token = jwtService.gerarToken(usuario);
        return new TokenResponse(token);
    }
}
