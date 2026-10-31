package com.primeschool.matricula.web;

import com.primeschool.matricula.exception.RecursoNoEncontradoException;
import com.primeschool.matricula.exception.ReglaNegocioException;
import com.primeschool.matricula.model.entity.Usuario;
import com.primeschool.matricula.security.UsuarioSesion;
import com.primeschool.matricula.service.AlertaService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador de consulta y atención de alertas tempranas.
 */
@Controller
public class AlertaController {

    private final AlertaService alertaService;
    private final UsuarioSesion usuarioSesion;

    /**
     * Crea el controlador con el servicio de alertas y la sesión.
     *
     * @param alertaService servicio de alertas
     * @param usuarioSesion accesor del usuario autenticado
     */
    public AlertaController(AlertaService alertaService, UsuarioSesion usuarioSesion) {
        this.alertaService = alertaService;
        this.usuarioSesion = usuarioSesion;
    }

    /**
     * Muestra las alertas visibles para el usuario autenticado.
     *
     * @param authentication autenticación de Spring Security
     * @param model modelo de la vista
     * @return nombre de la vista de alertas
     */
    @GetMapping("/alertas")
    public String listar(Authentication authentication, Model model) {
        Usuario usuario = usuarioSesion.actual(authentication);
        model.addAttribute("alertas", alertaService.listarVisibles(usuario));
        model.addAttribute("puedeAtender",
                !"APODERADO".equalsIgnoreCase(usuario.getRol().getNombreRol()));
        return "alertas/lista";
    }

    /**
     * Marca una alerta como atendida.
     *
     * @param id identificador de la alerta
     * @param authentication autenticación de Spring Security
     * @param redirectAttributes atributos para la redirección
     * @return redirección al listado de alertas
     */
    @PostMapping("/alertas/{id}/atender")
    public String atender(@PathVariable Integer id,
                          Authentication authentication,
                          RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioSesion.actual(authentication);
        try {
            alertaService.atender(id, usuario);
            redirectAttributes.addFlashAttribute("ok", "Alerta marcada como atendida");
        } catch (ReglaNegocioException | RecursoNoEncontradoException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/alertas";
    }
}
