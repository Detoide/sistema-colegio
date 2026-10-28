package com.primeschool.matricula.web;

import com.primeschool.matricula.dto.NotaFilaDTO;
import com.primeschool.matricula.dto.NotaForm;
import com.primeschool.matricula.exception.RecursoNoEncontradoException;
import com.primeschool.matricula.exception.ReglaNegocioException;
import com.primeschool.matricula.model.entity.PeriodoAcademico;
import com.primeschool.matricula.model.entity.Seccion;
import com.primeschool.matricula.model.entity.Usuario;
import com.primeschool.matricula.security.UsuarioSesion;
import com.primeschool.matricula.service.CatalogoService;
import com.primeschool.matricula.service.NotaService;
import com.primeschool.matricula.service.PeriodoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class NotasController {

    private final NotaService notaService;
    private final CatalogoService catalogoService;
    private final PeriodoService periodoService;
    private final UsuarioSesion usuarioSesion;

    public NotasController(NotaService notaService,
                           CatalogoService catalogoService,
                           PeriodoService periodoService,
                           UsuarioSesion usuarioSesion) {
        this.notaService = notaService;
        this.catalogoService = catalogoService;
        this.periodoService = periodoService;
        this.usuarioSesion = usuarioSesion;
    }

    @GetMapping("/notas")
    public String planilla(@RequestParam(required = false) Integer seccionId,
                           @RequestParam(required = false) Integer periodoId,
                           @RequestParam(required = false) Integer cursoId,
                           Authentication authentication,
                           Model model) {
        Usuario usuario = usuarioSesion.actual(authentication);
        List<Seccion> secciones = seccionesDe(usuario);
        List<PeriodoAcademico> periodos = periodoService.listar().stream()
                .filter(p -> !periodoService.esAnual(p))
                .toList();

        model.addAttribute("secciones", secciones);
        model.addAttribute("periodos", periodos);
        model.addAttribute("cursos", catalogoService.listarCursos());
        model.addAttribute("seccionId", seccionId);
        model.addAttribute("periodoId", periodoId);
        model.addAttribute("cursoId", cursoId);
        model.addAttribute("puedeEditar", "DOCENTE".equalsIgnoreCase(usuario.getRol().getNombreRol()));

        List<NotaFilaDTO> filas = List.of();
        if (seccionId != null && periodoId != null && cursoId != null) {
            filas = notaService.planilla(seccionId, periodoId, cursoId, usuario);
        }
        model.addAttribute("filas", filas);
        return "notas/planilla";
    }

    @PostMapping("/notas")
    public String guardar(@ModelAttribute("notaForm") NotaForm form,
                          Authentication authentication,
                          RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioSesion.actual(authentication);
        try {
            notaService.guardar(form, usuario);
            redirectAttributes.addFlashAttribute("ok", "Notas guardadas correctamente");
        } catch (ReglaNegocioException | RecursoNoEncontradoException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        String redirect = "redirect:/notas";
        if (form.getSeccionId() != null && form.getPeriodoId() != null && form.getCursoId() != null) {
            redirect += "?seccionId=" + form.getSeccionId()
                    + "&periodoId=" + form.getPeriodoId()
                    + "&cursoId=" + form.getCursoId();
        }
        return redirect;
    }

    private List<Seccion> seccionesDe(Usuario usuario) {
        if ("DOCENTE".equalsIgnoreCase(usuario.getRol().getNombreRol())) {
            return catalogoService.listarSeccionesPorTutor(usuario.getIdUsuario());
        }
        return catalogoService.listarSecciones();
    }
}
