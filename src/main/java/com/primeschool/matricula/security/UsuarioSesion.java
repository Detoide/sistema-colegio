package com.primeschool.matricula.security;

import com.primeschool.matricula.model.entity.Usuario;
import com.primeschool.matricula.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class UsuarioSesion {

    private final UsuarioService usuarioService;

    public UsuarioSesion(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public Usuario actual(Authentication authentication) {
        return usuarioService.obtenerPorCorreo(authentication.getName());
    }
}
