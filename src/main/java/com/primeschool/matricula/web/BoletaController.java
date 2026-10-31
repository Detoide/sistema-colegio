package com.primeschool.matricula.web;

import com.primeschool.matricula.exception.RecursoNoEncontradoException;
import com.primeschool.matricula.exception.ReglaNegocioException;
import com.primeschool.matricula.model.entity.Usuario;
import com.primeschool.matricula.security.UsuarioSesion;
import com.primeschool.matricula.service.BoletaService;
import com.primeschool.matricula.service.MatriculaService;
import com.primeschool.matricula.service.PeriodoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador de consulta y generación de boletas.
 */
@Controller
public class BoletaController {

    private final BoletaService boletaService;
    private final MatriculaService matriculaService;
    private final PeriodoService periodoService;
    private final UsuarioSesion usuarioSesion;

    /**
     * Crea el controlador con los servicios de boletas, matrículas y periodos.
     *
     * @param boletaService servicio de boletas
     * @param matriculaService servicio de matrículas
     * @param periodoService servicio de periodos
     * @param usuarioSesion accesor del usuario autenticado
     */
    public BoletaController(BoletaService boletaService,
                            MatriculaService matriculaService,
                            PeriodoService periodoService,
                            UsuarioSesion usuarioSesion) {
        this.boletaService = boletaService;
        this.matriculaService = matriculaService;
        this.periodoService = periodoService;
        this.usuarioSesion = usuarioSesion;
    }

    /**
     * Muestra la boleta de una matrícula para el periodo seleccionado.
     *
     * @param matriculaId identificador de la matrícula
     * @param periodoId identificador del periodo
     * @param authentication autenticación de Spring Security
     * @param model modelo de la vista
     * @return nombre de la vista de boletas
     */
    @GetMapping("/boletas")
    public String boletas(@RequestParam(required = false) Integer matriculaId,
                          @RequestParam(required = false) Integer periodoId,
                          Authentication authentication,
                          Model model) {
        Usuario usuario = usuarioSesion.actual(authentication);
        model.addAttribute("matriculas", matriculaService.listarVisibles(usuario));
        model.addAttribute("periodos", periodoService.listar().stream()
                .filter(p -> !periodoService.esAnual(p))
                .toList());
        model.addAttribute("matriculaId", matriculaId);
        model.addAttribute("periodoId", periodoId);

        if (matriculaId != null && periodoId != null) {
            try {
                model.addAttribute("boleta", boletaService.generar(matriculaId, periodoId, usuario));
            } catch (ReglaNegocioException | RecursoNoEncontradoException ex) {
                model.addAttribute("error", ex.getMessage());
            }
        }
        return "boletas/boleta";
    }
}
