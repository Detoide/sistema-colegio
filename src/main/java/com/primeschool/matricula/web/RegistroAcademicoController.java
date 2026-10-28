package com.primeschool.matricula.web;

import com.primeschool.matricula.dto.RegistroAcademicoFilaDTO;
import com.primeschool.matricula.dto.RegistroAcademicoForm;
import com.primeschool.matricula.exception.RecursoNoEncontradoException;
import com.primeschool.matricula.exception.ReglaNegocioException;
import com.primeschool.matricula.model.entity.PeriodoAcademico;
import com.primeschool.matricula.model.entity.Seccion;
import com.primeschool.matricula.model.entity.Usuario;
import com.primeschool.matricula.model.enums.EstadoAsistencia;
import com.primeschool.matricula.security.UsuarioSesion;
import com.primeschool.matricula.service.CatalogoService;
import com.primeschool.matricula.service.PeriodoService;
import com.primeschool.matricula.service.RegistroAcademicoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
public class RegistroAcademicoController {

    private final RegistroAcademicoService registroAcademicoService;
    private final CatalogoService catalogoService;
    private final PeriodoService periodoService;
    private final UsuarioSesion usuarioSesion;

    public RegistroAcademicoController(RegistroAcademicoService registroAcademicoService,
                                       CatalogoService catalogoService,
                                       PeriodoService periodoService,
                                       UsuarioSesion usuarioSesion) {
        this.registroAcademicoService = registroAcademicoService;
        this.catalogoService = catalogoService;
        this.periodoService = periodoService;
        this.usuarioSesion = usuarioSesion;
    }

    @GetMapping("/registro-academico")
    public String planilla(@RequestParam(required = false) Integer seccionId,
                           @RequestParam(required = false) Integer periodoId,
                           @RequestParam(required = false) Integer cursoId,
                           @RequestParam(required = false)
                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                           Authentication authentication,
                           Model model) {
        Usuario usuario = usuarioSesion.actual(authentication);
        LocalDate dia = fecha != null ? fecha : LocalDate.now();
        List<Seccion> secciones = seccionesDe(usuario);
        List<PeriodoAcademico> periodos = periodoService.listar().stream()
                .filter(p -> !periodoService.esAnual(p))
                .toList();

        model.addAttribute("secciones", secciones);
        model.addAttribute("periodos", periodos);
        model.addAttribute("cursos", catalogoService.listarCursos());
        model.addAttribute("estados", EstadoAsistencia.values());
        model.addAttribute("seccionId", seccionId);
        model.addAttribute("periodoId", periodoId);
        model.addAttribute("cursoId", cursoId);
        model.addAttribute("fecha", dia);
        model.addAttribute("puedeEditar", "DOCENTE".equalsIgnoreCase(usuario.getRol().getNombreRol()));

        List<RegistroAcademicoFilaDTO> filas = List.of();
        if (seccionId != null && periodoId != null && cursoId != null) {
            try {
                filas = registroAcademicoService.planilla(seccionId, periodoId, cursoId, dia, usuario);
            } catch (ReglaNegocioException | RecursoNoEncontradoException ex) {
                model.addAttribute("error", ex.getMessage());
            }
        }
        model.addAttribute("filas", filas);
        return "registro-academico/planilla";
    }

    @PostMapping("/registro-academico")
    public String guardar(@ModelAttribute("registroForm") RegistroAcademicoForm form,
                          Authentication authentication,
                          RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioSesion.actual(authentication);
        try {
            registroAcademicoService.guardar(form, usuario);
            redirectAttributes.addFlashAttribute("ok", "Registro académico guardado correctamente");
        } catch (ReglaNegocioException | RecursoNoEncontradoException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        StringBuilder redirect = new StringBuilder("redirect:/registro-academico");
        if (form.getSeccionId() != null && form.getPeriodoId() != null
                && form.getCursoId() != null && form.getFecha() != null) {
            redirect.append("?seccionId=").append(form.getSeccionId())
                    .append("&periodoId=").append(form.getPeriodoId())
                    .append("&cursoId=").append(form.getCursoId())
                    .append("&fecha=").append(form.getFecha());
        }
        return redirect.toString();
    }

    private List<Seccion> seccionesDe(Usuario usuario) {
        if ("DOCENTE".equalsIgnoreCase(usuario.getRol().getNombreRol())) {
            return catalogoService.listarSeccionesPorTutor(usuario.getIdUsuario());
        }
        return catalogoService.listarSecciones();
    }
}
