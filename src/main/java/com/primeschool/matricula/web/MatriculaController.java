package com.primeschool.matricula.web;

import com.primeschool.matricula.dto.MatriculaRequest;
import com.primeschool.matricula.exception.RecursoNoEncontradoException;
import com.primeschool.matricula.exception.ReglaNegocioException;
import com.primeschool.matricula.model.entity.Usuario;
import com.primeschool.matricula.security.UsuarioSesion;
import com.primeschool.matricula.service.CatalogoService;
import com.primeschool.matricula.service.EstudianteService;
import com.primeschool.matricula.service.MatriculaService;
import com.primeschool.matricula.service.PeriodoService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador de listado, registro y gestión de matrículas.
 */
@Controller
public class MatriculaController {

    private final MatriculaService matriculaService;
    private final EstudianteService estudianteService;
    private final CatalogoService catalogoService;
    private final PeriodoService periodoService;
    private final UsuarioSesion usuarioSesion;

    /**
     * Crea el controlador con los servicios de matrícula y catálogos.
     *
     * @param matriculaService servicio de matrículas
     * @param estudianteService servicio de estudiantes
     * @param catalogoService servicio de catálogos
     * @param periodoService servicio de periodos
     * @param usuarioSesion accesor del usuario autenticado
     */
    public MatriculaController(MatriculaService matriculaService,
                               EstudianteService estudianteService,
                               CatalogoService catalogoService,
                               PeriodoService periodoService,
                               UsuarioSesion usuarioSesion) {
        this.matriculaService = matriculaService;
        this.estudianteService = estudianteService;
        this.catalogoService = catalogoService;
        this.periodoService = periodoService;
        this.usuarioSesion = usuarioSesion;
    }

    /**
     * Muestra las matrículas visibles para el usuario autenticado.
     *
     * @param authentication autenticación de Spring Security
     * @param model modelo de la vista
     * @return nombre de la vista de matrículas
     */
    @GetMapping("/matriculas")
    public String listar(Authentication authentication, Model model) {
        Usuario usuario = usuarioSesion.actual(authentication);
        model.addAttribute("matriculas", matriculaService.listarVisibles(usuario));
        return "matriculas/lista";
    }

    /**
     * Muestra el formulario para registrar una nueva matrícula.
     *
     * @param model modelo de la vista
     * @return nombre de la vista del formulario
     */
    @GetMapping("/matriculas/nueva")
    public String nueva(Model model) {
        model.addAttribute("matricula", new MatriculaRequest());
        cargarCombos(model);
        return "matriculas/form";
    }

    /**
     * Registra una nueva matrícula a partir de los datos del formulario.
     *
     * @param request datos de la matrícula
     * @param bindingResult resultado de la validación
     * @param model modelo de la vista
     * @param redirectAttributes atributos para la redirección
     * @return redirección al listado o vista del formulario con errores
     */
    @PostMapping("/matriculas")
    public String registrar(@Valid @ModelAttribute("matricula") MatriculaRequest request,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            boolean errorEnPaso1 = bindingResult.getFieldError("periodoId") != null
                    || bindingResult.getFieldError("seccionId") != null;
            model.addAttribute("paso", errorEnPaso1 ? 1 : 2);
            cargarCombos(model);
            return "matriculas/form";
        }
        try {
            matriculaService.registrar(request);
            redirectAttributes.addFlashAttribute("ok", "Matrícula registrada correctamente");
            return "redirect:/matriculas";
        } catch (ReglaNegocioException | RecursoNoEncontradoException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("paso", 2);
            cargarCombos(model);
            return "matriculas/form";
        }
    }

    /**
     * Confirma una matrícula pendiente.
     *
     * @param id identificador de la matrícula
     * @param redirectAttributes atributos para la redirección
     * @return redirección al listado de matrículas
     */
    @PostMapping("/matriculas/{id}/confirmar")
    public String confirmar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        matriculaService.confirmar(id);
        redirectAttributes.addFlashAttribute("ok", "Matrícula confirmada");
        return "redirect:/matriculas";
    }

    /**
     * Anula una matrícula y libera su vacante.
     *
     * @param id identificador de la matrícula
     * @param redirectAttributes atributos para la redirección
     * @return redirección al listado de matrículas
     */
    @PostMapping("/matriculas/{id}/anular")
    public String anular(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        matriculaService.anular(id);
        redirectAttributes.addFlashAttribute("ok", "Matrícula anulada y vacante liberada");
        return "redirect:/matriculas";
    }

    private void cargarCombos(Model model) {
        model.addAttribute("estudiantes", estudianteService.listar());
        model.addAttribute("grados", catalogoService.listarGrados());
        model.addAttribute("secciones", catalogoService.listarSecciones());
        model.addAttribute("periodos", periodoService.listar().stream()
                .filter(periodoService::esAnual)
                .toList());
    }
}
