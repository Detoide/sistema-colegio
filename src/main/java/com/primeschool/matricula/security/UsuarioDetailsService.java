package com.primeschool.matricula.security;

import com.primeschool.matricula.model.entity.Usuario;
import com.primeschool.matricula.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        String normalizado = correo == null ? "" : correo.trim().toLowerCase();
        Usuario usuario = usuarioRepository.findByCorreo(normalizado)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return User.withUsername(usuario.getCorreo())
                .password(usuario.getContrasenaHash())
                .roles(usuario.getRol().getNombreRol().toUpperCase())
                .disabled(!Boolean.TRUE.equals(usuario.getActivo()))
                .build();
    }
}
