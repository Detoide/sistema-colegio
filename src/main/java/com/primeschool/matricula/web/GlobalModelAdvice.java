package com.primeschool.matricula.web;

import com.primeschool.matricula.security.UsuarioSesion;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Agrega datos comunes a todas las vistas, como el usuario autenticado.
 */
@ControllerAdvice
public class GlobalModelAdvice {

    private final UsuarioSesion usuarioSesion;

    /**
     * Crea el componente con el accesor de la sesión.
     *
     * @param usuarioSesion accesor del usuario autenticado
     */
    public GlobalModelAdvice(UsuarioSesion usuarioSesion) {
        this.usuarioSesion = usuarioSesion;
    }

    /**
     * Incorpora el usuario actual al modelo cuando la sesión está autenticada.
     *
     * @param model modelo de la vista
     * @param authentication autenticación de Spring Security
     */
    @ModelAttribute
    public void agregarUsuario(Model model, Authentication authentication) {
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            model.addAttribute("usuarioActual", usuarioSesion.actual(authentication));
        }
    }
}
