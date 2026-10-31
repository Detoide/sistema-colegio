package com.primeschool.matricula.web;

import com.primeschool.matricula.dto.EstudianteRequest;
import com.primeschool.matricula.exception.RecursoNoEncontradoException;
import com.primeschool.matricula.exception.ReglaNegocioException;
import com.primeschool.matricula.model.entity.Estudiante;
import com.primeschool.matricula.service.ApoderadoService;
import com.primeschool.matricula.service.EstudianteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador de listado y registro de estudiantes.
 */
@Controller
public class EstudianteController {

    private final EstudianteService estudianteService;
    private final ApoderadoService apoderadoService;

    /**
     * Crea el controlador con los servicios de estudiantes y apoderados.
     *
     * @param estudianteService servicio de estudiantes
     * @param apoderadoService servicio de apoderados
     */
    public EstudianteController(EstudianteService estudianteService, ApoderadoService apoderadoService) {
        this.estudianteService = estudianteService;
        this.apoderadoService = apoderadoService;
    }

    /**
     * Muestra el listado de estudiantes y permite buscarlos.
     *
     * @param q texto de búsqueda opcional
     * @param model modelo de la vista
     * @return nombre de la vista de estudiantes
     */
    @GetMapping("/estudiantes")
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("estudiantes", estudianteService.buscar(q));
        model.addAttribute("apoderados", apoderadoService.listar());
        model.addAttribute("estudiante", new EstudianteRequest());
        model.addAttribute("q", q);
        return "estudiantes/lista";
    }

    /**
     * Carga un estudiante en el formulario para su edición.
     *
     * @param id identificador del estudiante
     * @param model modelo de la vista
     * @return nombre de la vista de estudiantes
     */
    @GetMapping("/estudiantes/{id}/editar")
    public String editar(@PathVariable Integer id, Model model) {
        Estudiante estudiante = estudianteService.obtener(id);
        EstudianteRequest request = new EstudianteRequest();
        request.setIdEstudiante(estudiante.getIdEstudiante());
        request.setApoderadoId(estudiante.getApoderado().getIdApoderado());
        request.setCodigo(estudiante.getCodigo());
        request.setNombres(estudiante.getNombres());
        request.setApellidos(estudiante.getApellidos());
        request.setFechaNacimiento(estudiante.getFechaNacimiento());
        model.addAttribute("estudiante", request);
        model.addAttribute("estudiantes", estudianteService.listar());
        model.addAttribute("apoderados", apoderadoService.listar());
        return "estudiantes/lista";
    }

    /**
     * Registra o actualiza un estudiante.
     *
     * @param request datos del estudiante
     * @param bindingResult resultado de la validación
     * @param model modelo de la vista
     * @param redirectAttributes atributos para la redirección
     * @return redirección al listado o vista del formulario con errores
     */
    @PostMapping("/estudiantes")
    public String guardar(@Valid @ModelAttribute("estudiante") EstudianteRequest request,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("estudiantes", estudianteService.listar());
            model.addAttribute("apoderados", apoderadoService.listar());
            return "estudiantes/lista";
        }
        try {
            estudianteService.guardar(request);
            redirectAttributes.addFlashAttribute("ok", "Estudiante guardado correctamente");
            return "redirect:/estudiantes";
        } catch (ReglaNegocioException | RecursoNoEncontradoException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("estudiantes", estudianteService.listar());
            model.addAttribute("apoderados", apoderadoService.listar());
            return "estudiantes/lista";
        }
    }
}
