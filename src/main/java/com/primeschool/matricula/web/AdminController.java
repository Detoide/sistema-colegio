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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AdminController {

    private static final List<String> ROLES = List.of("ADMINISTRADOR", "DIRECTIVO", "DOCENTE");

    private final UsuarioService usuarioService;
    private final PeriodoService periodoService;
    private final CatalogoService catalogoService;
    private final MatriculaService matriculaService;

    public AdminController(UsuarioService usuarioService,
                           PeriodoService periodoService,
                           CatalogoService catalogoService,
                           MatriculaService matriculaService) {
        this.usuarioService = usuarioService;
        this.periodoService = periodoService;
        this.catalogoService = catalogoService;
        this.matriculaService = matriculaService;
    }

    @GetMapping("/admin")
    public String index() {
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/admin/usuarios")
    public String usuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        model.addAttribute("roles", ROLES);
        model.addAttribute("usuarioForm", new UsuarioRequest());
        return "admin/usuarios";
    }

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

    @GetMapping("/admin/periodos")
    public String periodos(Model model) {
        model.addAttribute("periodos", periodoService.listar());
        model.addAttribute("periodo", new PeriodoForm());
        return "admin/periodos";
    }

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

    @GetMapping("/admin/catalogos")
    public String catalogos(Model model) {
        cargarCatalogos(model);
        return "admin/catalogos";
    }

    @PostMapping("/admin/grados")
    public String crearGrado(@RequestParam String nombreGrado,
                             @RequestParam String nivel,
                             RedirectAttributes redirectAttributes) {
        try {
            catalogoService.crearGrado(nombreGrado, nivel);
            redirectAttributes.addFlashAttribute("ok", "Grado creado correctamente");
        } catch (ReglaNegocioException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/catalogos";
    }

    @PostMapping("/admin/cursos")
    public String crearCurso(@RequestParam String nombreCurso,
                             @RequestParam(required = false) String competencia,
                             RedirectAttributes redirectAttributes) {
        try {
            catalogoService.crearCurso(nombreCurso, competencia);
            redirectAttributes.addFlashAttribute("ok", "Curso creado correctamente");
        } catch (ReglaNegocioException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/catalogos";
    }

    @PostMapping("/admin/secciones")
    public String crearSeccion(@RequestParam Integer gradoId,
                               @RequestParam Integer tutorId,
                               @RequestParam String nombreSeccion,
                               @RequestParam Integer vacantesTotales,
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
