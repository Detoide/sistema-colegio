package com.primeschool.matricula.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador de la página de inicio de sesión.
 */
@Controller
public class LoginController {

    /**
     * Muestra el formulario de inicio de sesión.
     *
     * @return nombre de la vista de login
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
