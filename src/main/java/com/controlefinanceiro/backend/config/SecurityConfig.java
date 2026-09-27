package com.controlefinanceiro.backend.config;

import com.controlefinanceiro.backend.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // BCrypt: algoritmo de hash desenhado especificamente para senhas — é
    // lento de propósito (para dificultar ataques de força bruta) e inclui
    // um "salt" aleatório automaticamente, então duas pessoas com a mesma
    // senha geram hashes diferentes. NUNCA armazene senha em texto puro, e
    // nunca use um hash genérico (MD5, SHA-256 sozinho) para senha.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Expõe o AuthenticationManager que o Spring monta automaticamente (ele
    // junta o AutenticacaoUsuarioService + o PasswordEncoder acima nos
    // bastidores). É este objeto que o AuthService vai usar no login para
    // checar e-mail + senha.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Vem de app.cors.allowed-origins no application.yml — em produção,
    // sobrescrita por uma variável de ambiente com a URL real do frontend
    // publicado, sem precisar recompilar nada.
    @Value("${app.cors.allowed-origins}")
    private String origensPermitidas;

    // CORS é uma checagem feita pelo NAVEGADOR, não pelo backend em si: sem
    // essa liberação explícita, o Chrome/Firefox bloqueiam a resposta antes
    // mesmo dela chegar ao código Angular, mesmo que o backend tenha
    // respondido normalmente. Ferramentas como curl e Postman ignoram CORS —
    // por isso os testes com curl funcionam sem essa configuração, mas o
    // navegador exige.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuracao = new CorsConfiguration();
        // Suporta uma ou mais origens separadas por vírgula, ex:
        // "http://localhost:4200,https://controle-financeiro.pages.dev"
        configuracao.setAllowedOrigins(List.of(origensPermitidas.split(",")));
        configuracao.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuracao.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuracao);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF protege contra ataques que abusam de sessão baseada em
                // cookie do navegador. Como aqui a autenticação é via token no
                // header Authorization (sem cookie de sessão), esse tipo de
                // ataque não se aplica, então desabilitamos.
                .csrf(csrf -> csrf.disable())

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // STATELESS: o Spring nunca cria HttpSession nem depende de
                // estado guardado no servidor entre requisições. Cada
                // requisição se autentica sozinha, com o token que ela traz.
                // É isso que torna a API fácil de escalar (não importa qual
                // instância do backend atende cada requisição).
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll() // cadastro e login não exigem token
                        .anyRequest().authenticated()            // todo o resto exige
                )

                // Insere nosso filtro ANTES do filtro padrão de autenticação
                // por usuário/senha do Spring Security, já que aqui quem
                // autentica a maioria das requisições é o token, não um
                // formulário de login tradicional.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
