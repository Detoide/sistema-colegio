package com.primeschool.matricula.web;

import com.primeschool.matricula.service.MatriculaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VacanteController {

    private final MatriculaService matriculaService;

    public VacanteController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @GetMapping("/vacantes")
    public String vacantes(Model model) {
        model.addAttribute("vacantes", matriculaService.consultarVacantes());
        return "vacantes/lista";
    }
}
