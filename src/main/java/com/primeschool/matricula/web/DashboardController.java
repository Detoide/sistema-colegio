package com.primeschool.matricula.web;

import com.primeschool.matricula.dto.DashboardDTO;
import com.primeschool.matricula.model.entity.Usuario;
import com.primeschool.matricula.security.UsuarioSesion;
import com.primeschool.matricula.service.DashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador del panel principal de la aplicación.
 */
@Controller
public class DashboardController {

    private final DashboardService dashboardService;
    private final UsuarioSesion usuarioSesion;

    /**
     * Crea el controlador con el servicio de dashboard y la sesión.
     *
     * @param dashboardService servicio del dashboard
     * @param usuarioSesion accesor del usuario autenticado
     */
    public DashboardController(DashboardService dashboardService, UsuarioSesion usuarioSesion) {
        this.dashboardService = dashboardService;
        this.usuarioSesion = usuarioSesion;
    }

    /**
     * Muestra el resumen del dashboard del usuario autenticado.
     *
     * @param authentication autenticación de Spring Security
     * @param model modelo de la vista
     * @return nombre de la vista del dashboard
     */
    @GetMapping("/")
    public String dashboard(Authentication authentication, Model model) {
        Usuario usuario = usuarioSesion.actual(authentication);
        DashboardDTO dto = dashboardService.resumen(usuario);
        model.addAttribute("dto", dto);
        return "dashboard";
    }
}
