package com.primeschool.matricula.security;

import com.primeschool.matricula.model.entity.Usuario;
import com.primeschool.matricula.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio de autenticación que carga los usuarios desde la base de datos
 * y los adapta al modelo de Spring Security.
 */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Crea el servicio con el repositorio de usuarios.
     *
     * @param usuarioRepository repositorio utilizado para buscar usuarios
     */
    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Busca un usuario por su correo y construye sus credenciales y roles.
     *
     * @param correo correo electrónico del usuario
     * @return detalles del usuario autenticado
     * @throws UsernameNotFoundException si el correo no corresponde a un usuario
     */
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
