package com.controlefinanceiro.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtService {

    // @Value injeta o valor da propriedade app.jwt.secret (definida no
    // application.yml) direto neste campo, quando o Spring cria o bean.
    @Value("${app.jwt.secret}")
    private String segredoBase64;

    @Value("${app.jwt.expiracao-minutos}")
    private long expiracaoMinutos;

    // A chave usada para ASSINAR o token (algoritmo HMAC-SHA). Assinar não é
    // criptografar: qualquer um pode ler o conteúdo de um JWT (é só Base64,
    // não é segredo), mas só quem tem esta chave consegue gerar uma
    // assinatura válida — é isso que impede alguém de forjar ou alterar um
    // token.
    private SecretKey chave() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(segredoBase64));
    }

    public String gerarToken(UsuarioAutenticado usuario) {
        Instant agora = Instant.now();

        return Jwts.builder()
                .subject(usuario.getUsername())            // "de quem" é o token (o e-mail)
                .claim("usuarioId", usuario.getId())        // dado extra: evita consultar o banco de novo para saber o id
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plus(expiracaoMinutos, ChronoUnit.MINUTES)))
                .signWith(chave())
                .compact();
    }

    public String extrairEmail(String token) {
        return extrairClaims(token).getSubject();
    }

    public Long extrairUsuarioId(String token) {
        return extrairClaims(token).get("usuarioId", Long.class);
    }

    // true se a assinatura confere e o token ainda não expirou. Qualquer
    // outro problema (assinatura inválida, token expirado, formato
    // incorreto) faz o parser lançar exceção — nós capturamos e devolvemos
    // "não é válido" em vez de deixar o erro estourar.
    public boolean valido(String token) {
        try {
            extrairClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims extrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(chave())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
