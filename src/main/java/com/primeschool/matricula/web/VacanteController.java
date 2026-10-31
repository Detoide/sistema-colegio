package com.primeschool.matricula.web;

import com.primeschool.matricula.service.MatriculaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador de consulta de vacantes por sección.
 */
@Controller
public class VacanteController {

    private final MatriculaService matriculaService;

    /**
     * Crea el controlador con el servicio de matrículas.
     *
     * @param matriculaService servicio de matrículas
     */
    public VacanteController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    /**
     * Muestra el listado de vacantes disponibles.
     *
     * @param model modelo de la vista
     * @return nombre de la vista de vacantes
     */
    @GetMapping("/vacantes")
    public String vacantes(Model model) {
        model.addAttribute("vacantes", matriculaService.consultarVacantes());
        return "vacantes/lista";
    }
}
