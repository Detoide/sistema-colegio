package com.primeschool.matricula.web;

import com.primeschool.matricula.dto.PeriodoForm;
import com.primeschool.matricula.dto.UsuarioRequest;
import com.primeschool.matricula.exception.RecursoNoEncontradoException;
import com.primeschool.matricula.exception.ReglaNegocioException;
import com.primeschool.matricula.service.CatalogoService;
import com.primeschool.matricula.service.MatriculaService;
import com.primeschool.matricula.service.PeriodoService;
import com.primeschool.matricula.service.UsuarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador de administración de usuarios, periodos y catálogos.
 */
@Controller
@Validated
public class AdminController {

    private static final List<String> ROLES = List.of("ADMINISTRADOR", "DIRECTIVO", "DOCENTE");

    private final UsuarioService usuarioService;
    private final PeriodoService periodoService;
    private final CatalogoService catalogoService;
    private final MatriculaService matriculaService;

    /**
     * Crea el controlador con los servicios de administración.
     *
     * @param usuarioService servicio de usuarios
     * @param periodoService servicio de periodos
     * @param catalogoService servicio de catálogos
     * @param matriculaService servicio de matrículas
     */
    public AdminController(UsuarioService usuarioService,
                           PeriodoService periodoService,
                           CatalogoService catalogoService,
                           MatriculaService matriculaService) {
        this.usuarioService = usuarioService;
        this.periodoService = periodoService;
        this.catalogoService = catalogoService;
        this.matriculaService = matriculaService;
    }

    /**
     * Redirige al módulo principal de administración.
     *
     * @return redirección al listado de usuarios
     */
    @GetMapping("/admin")
    public String index() {
        return "redirect:/admin/usuarios";
    }

    /**
     * Muestra el listado y formulario de usuarios.
     *
     * @param model modelo de la vista
     * @return nombre de la vista de usuarios
     */
    @GetMapping("/admin/usuarios")
    public String usuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        model.addAttribute("roles", ROLES);
        model.addAttribute("usuarioForm", new UsuarioRequest());
        return "admin/usuarios";
    }

    /**
     * Crea un nuevo usuario del sistema.
     *
     * @param request datos del usuario
     * @param bindingResult resultado de la validación
     * @param model modelo de la vista
     * @param redirectAttributes atributos para la redirección
     * @return redirección al listado o vista del formulario con errores
     */
    @PostMapping("/admin/usuarios")
    public String crearUsuario(@Valid @ModelAttribute("usuarioForm") UsuarioRequest request,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("usuarios", usuarioService.listar());
            model.addAttribute("roles", ROLES);
            return "admin/usuarios";
        }
        try {
            usuarioService.crear(request);
            redirectAttributes.addFlashAttribute("ok", "Usuario creado correctamente");
            return "redirect:/admin/usuarios";
        } catch (ReglaNegocioException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("usuarios", usuarioService.listar());
            model.addAttribute("roles", ROLES);
            return "admin/usuarios";
        }
    }

    /**
     * Muestra el listado y formulario de periodos académicos.
     *
     * @param model modelo de la vista
     * @return nombre de la vista de periodos
     */
    @GetMapping("/admin/periodos")
    public String periodos(Model model) {
        model.addAttribute("periodos", periodoService.listar());
        model.addAttribute("periodo", new PeriodoForm());
        return "admin/periodos";
    }

    /**
     * Crea un nuevo periodo académico.
     *
     * @param request datos del periodo
     * @param bindingResult resultado de la validación
     * @param model modelo de la vista
     * @param redirectAttributes atributos para la redirección
     * @return redirección al listado o vista del formulario con errores
     */
    @PostMapping("/admin/periodos")
    public String crearPeriodo(@Valid @ModelAttribute("periodo") PeriodoForm request,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("periodos", periodoService.listar());
            return "admin/periodos";
        }
        try {
            periodoService.crear(request.getNombre(), request.getFechaInicio(), request.getFechaFin());
            redirectAttributes.addFlashAttribute("ok", "Periodo creado correctamente");
            return "redirect:/admin/periodos";
        } catch (ReglaNegocioException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("periodos", periodoService.listar());
            return "admin/periodos";
        }
    }

    /**
     * Muestra el listado y formularios de catálogos académicos.
     *
     * @param model modelo de la vista
     * @return nombre de la vista de catálogos
     */
    @GetMapping("/admin/catalogos")
    public String catalogos(Model model) {
        cargarCatalogos(model);
        return "admin/catalogos";
    }

    /**
     * Crea un nuevo grado académico.
     *
     * @param nombreGrado nombre del grado
     * @param nivel nivel académico
     * @param redirectAttributes atributos para la redirección
     * @return redirección al módulo de catálogos
     */
    @PostMapping("/admin/grados")
    public String crearGrado(@RequestParam @NotBlank @Size(max = 30) String nombreGrado,
                             @RequestParam @NotBlank @Size(max = 20) String nivel,
                             RedirectAttributes redirectAttributes) {
        try {
            catalogoService.crearGrado(nombreGrado, nivel);
            redirectAttributes.addFlashAttribute("ok", "Grado creado correctamente");
        } catch (ReglaNegocioException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/catalogos";
    }

    /**
     * Crea un nuevo curso.
     *
     * @param nombreCurso nombre del curso
     * @param competencia competencia asociada
     * @param redirectAttributes atributos para la redirección
     * @return redirección al módulo de catálogos
     */
    @PostMapping("/admin/cursos")
    public String crearCurso(@RequestParam @NotBlank @Size(max = 80) String nombreCurso,
                             @RequestParam(required = false) @Size(max = 150) String competencia,
                             RedirectAttributes redirectAttributes) {
        try {
            catalogoService.crearCurso(nombreCurso, competencia);
            redirectAttributes.addFlashAttribute("ok", "Curso creado correctamente");
        } catch (ReglaNegocioException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/catalogos";
    }

    /**
     * Crea una nueva sección con su capacidad de vacantes.
     *
     * @param gradoId identificador del grado
     * @param tutorId identificador del docente tutor
     * @param nombreSeccion nombre de la sección
     * @param vacantesTotales cantidad total de vacantes
     * @param redirectAttributes atributos para la redirección
     * @return redirección al módulo de catálogos
     */
    @PostMapping("/admin/secciones")
    public String crearSeccion(@RequestParam @NotNull Integer gradoId,
                               @RequestParam @NotNull Integer tutorId,
                               @RequestParam @NotBlank @Size(max = 5) String nombreSeccion,
                               @RequestParam @NotNull @Min(1) Integer vacantesTotales,
                               RedirectAttributes redirectAttributes) {
        try {
            catalogoService.crearSeccion(gradoId, tutorId, nombreSeccion, vacantesTotales);
            redirectAttributes.addFlashAttribute("ok", "Sección creada correctamente");
        } catch (ReglaNegocioException | RecursoNoEncontradoException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/catalogos";
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("grados", catalogoService.listarGrados());
        model.addAttribute("cursos", catalogoService.listarCursos());
        model.addAttribute("secciones", catalogoService.listarSecciones());
        model.addAttribute("docentes", usuarioService.listarDocentes());
        model.addAttribute("vacantes", matriculaService.consultarVacantes());
    }
}
