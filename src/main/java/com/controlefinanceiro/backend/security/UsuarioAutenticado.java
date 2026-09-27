package com.controlefinanceiro.backend.security;

import com.controlefinanceiro.backend.entity.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// O Spring Security não conhece a sua classe Usuario — ele conhece a
// interface UserDetails. Esta classe é o "adaptador" entre os dois mundos:
// ela embrulha um Usuario e expõe o que o Spring Security precisa (username,
// password, se a conta está ativa etc.), além de guardar o id, que os
// Controllers vão usar para saber QUEM está fazendo a requisição.
public class UsuarioAutenticado implements UserDetails {

    private final Long id;
    private final String email;
    private final String senhaHash;

    public UsuarioAutenticado(Usuario usuario) {
        this.id = usuario.getId();
        this.email = usuario.getEmail();
        this.senhaHash = usuario.getSenhaHash();
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return email; // no nosso sistema, o "username" de login é o e-mail
    }

    @Override
    public String getPassword() {
        return senhaHash;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Ainda não temos papéis/perfis (admin, usuário comum etc.) — todo
        // usuário autenticado tem o mesmo nível de acesso aos PRÓPRIOS dados.
        // Se um dia precisar de perfis, é aqui que eles entram.
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
