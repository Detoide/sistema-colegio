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

@Controller
public class AlertaController {

    private final AlertaService alertaService;
    private final UsuarioSesion usuarioSesion;

    public AlertaController(AlertaService alertaService, UsuarioSesion usuarioSesion) {
        this.alertaService = alertaService;
        this.usuarioSesion = usuarioSesion;
    }

    @GetMapping("/alertas")
    public String listar(Authentication authentication, Model model) {
        Usuario usuario = usuarioSesion.actual(authentication);
        model.addAttribute("alertas", alertaService.listarVisibles(usuario));
        model.addAttribute("puedeAtender",
                !"APODERADO".equalsIgnoreCase(usuario.getRol().getNombreRol()));
        return "alertas/lista";
    }

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
