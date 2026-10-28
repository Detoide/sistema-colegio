package com.primeschool.matricula.web;

import com.primeschool.matricula.security.UsuarioSesion;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    private final UsuarioSesion usuarioSesion;

    public GlobalModelAdvice(UsuarioSesion usuarioSesion) {
        this.usuarioSesion = usuarioSesion;
    }

    @ModelAttribute
    public void agregarUsuario(Model model, Authentication authentication) {
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            model.addAttribute("usuarioActual", usuarioSesion.actual(authentication));
        }
    }
}
