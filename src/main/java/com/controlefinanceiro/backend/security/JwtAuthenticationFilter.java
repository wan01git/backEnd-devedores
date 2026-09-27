package com.controlefinanceiro.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// OncePerRequestFilter garante que a lógica abaixo roda uma única vez por
// requisição (em algumas configurações um filtro poderia ser acionado mais
// de uma vez para a mesma requisição — esta classe evita isso).
//
// Este filtro roda ANTES do Spring decidir se libera ou bloqueia a
// requisição. O trabalho dele é: se vier um token válido no header
// Authorization, avisar o Spring Security "este usuário está autenticado"
// preenchendo o SecurityContext. Se não vier token, ou o token for
// inválido, o filtro simplesmente deixa a requisição seguir sem autenticar
// — quem decide se isso é aceitável é a configuração de rotas no
// SecurityConfig (a próxima classe).
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AutenticacaoUsuarioService autenticacaoUsuarioService;

    public JwtAuthenticationFilter(JwtService jwtService, AutenticacaoUsuarioService autenticacaoUsuarioService) {
        this.jwtService = jwtService;
        this.autenticacaoUsuarioService = autenticacaoUsuarioService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // Convenção padrão: o token vem como "Authorization: Bearer <token>".
        // Sem esse header no formato certo, não há o que autenticar aqui.
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7); // remove o prefixo "Bearer "

        if (jwtService.valido(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
            String email = jwtService.extrairEmail(token);
            UserDetails usuario = autenticacaoUsuarioService.loadUserByUsername(email);

            var authentication = new UsernamePasswordAuthenticationToken(
                    usuario, null, usuario.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // A partir daqui, para o resto desta requisição, o Spring
            // Security considera o usuário autenticado.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
