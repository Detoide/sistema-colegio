package com.primeschool.matricula.security;

import com.primeschool.matricula.model.entity.Usuario;
import com.primeschool.matricula.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Obtiene el usuario de la aplicación asociado a la sesión autenticada.
 */
@Component
public class UsuarioSesion {

    private final UsuarioService usuarioService;

    /**
     * Crea el componente con el servicio de usuarios.
     *
     * @param usuarioService servicio utilizado para recuperar el usuario
     */
    public UsuarioSesion(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Recupera el usuario correspondiente a la autenticación actual.
     *
     * @param authentication autenticación de Spring Security
     * @return usuario asociado al correo autenticado
     */
    public Usuario actual(Authentication authentication) {
        return usuarioService.obtenerPorCorreo(authentication.getName());
    }
}
