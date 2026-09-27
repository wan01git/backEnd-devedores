package com.controlefinanceiro.backend.security;

import com.controlefinanceiro.backend.entity.Usuario;
import com.controlefinanceiro.backend.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// UserDetailsService é uma interface do próprio Spring Security. Ao existir
// exatamente UM bean desse tipo no projeto (esta classe), o Spring Security
// o usa automaticamente para: (1) no login, buscar o usuário pelo e-mail
// e comparar a senha; (2) no filtro JWT, recarregar os dados do usuário a
// partir do e-mail contido no token.
@Service
public class AutenticacaoUsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public AutenticacaoUsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("usuário não encontrado"));
        return new UsuarioAutenticado(usuario);
    }
}
