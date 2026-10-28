package com.primeschool.matricula.web;

import com.primeschool.matricula.dto.ApoderadoRequest;
import com.primeschool.matricula.exception.ReglaNegocioException;
import com.primeschool.matricula.service.ApoderadoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ApoderadoController {

    private final ApoderadoService apoderadoService;

    public ApoderadoController(ApoderadoService apoderadoService) {
        this.apoderadoService = apoderadoService;
    }

    @GetMapping("/apoderados")
    public String listar(Model model) {
        model.addAttribute("apoderados", apoderadoService.listar());
        model.addAttribute("apoderado", new ApoderadoRequest());
        return "apoderados/lista";
    }

    @PostMapping("/apoderados")
    public String crear(@Valid @ModelAttribute("apoderado") ApoderadoRequest request,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("apoderados", apoderadoService.listar());
            return "apoderados/lista";
        }
        try {
            apoderadoService.crear(request);
            redirectAttributes.addFlashAttribute("ok", "Apoderado registrado correctamente");
            return "redirect:/apoderados";
        } catch (ReglaNegocioException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("apoderados", apoderadoService.listar());
            return "apoderados/lista";
        }
    }
}
