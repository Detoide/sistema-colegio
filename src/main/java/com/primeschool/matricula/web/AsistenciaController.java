package com.primeschool.matricula.web;

import com.primeschool.matricula.dto.AsistenciaFilaDTO;
import com.primeschool.matricula.dto.AsistenciaForm;
import com.primeschool.matricula.exception.RecursoNoEncontradoException;
import com.primeschool.matricula.exception.ReglaNegocioException;
import com.primeschool.matricula.model.entity.Seccion;
import com.primeschool.matricula.model.entity.Usuario;
import com.primeschool.matricula.model.enums.EstadoAsistencia;
import com.primeschool.matricula.security.UsuarioSesion;
import com.primeschool.matricula.service.AsistenciaService;
import com.primeschool.matricula.service.CatalogoService;
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
public class AsistenciaController {

    private final AsistenciaService asistenciaService;
    private final CatalogoService catalogoService;
    private final UsuarioSesion usuarioSesion;

    public AsistenciaController(AsistenciaService asistenciaService,
                                CatalogoService catalogoService,
                                UsuarioSesion usuarioSesion) {
        this.asistenciaService = asistenciaService;
        this.catalogoService = catalogoService;
        this.usuarioSesion = usuarioSesion;
    }

    @GetMapping("/asistencia")
    public String planilla(@RequestParam(required = false) Integer seccionId,
                           @RequestParam(required = false)
                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                           Authentication authentication,
                           Model model) {
        Usuario usuario = usuarioSesion.actual(authentication);
        LocalDate dia = fecha != null ? fecha : LocalDate.now();
        List<Seccion> secciones = seccionesDe(usuario);

        model.addAttribute("secciones", secciones);
        model.addAttribute("seccionId", seccionId);
        model.addAttribute("fecha", dia);
        model.addAttribute("estados", EstadoAsistencia.values());
        model.addAttribute("puedeEditar", "DOCENTE".equalsIgnoreCase(usuario.getRol().getNombreRol()));

        List<AsistenciaFilaDTO> filas = List.of();
        if (seccionId != null) {
            filas = asistenciaService.planilla(seccionId, dia, usuario);
        }
        model.addAttribute("filas", filas);
        return "asistencia/planilla";
    }

    @PostMapping("/asistencia")
    public String guardar(@ModelAttribute("asistenciaForm") AsistenciaForm form,
                          Authentication authentication,
                          RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioSesion.actual(authentication);
        try {
            asistenciaService.guardar(form, usuario);
            redirectAttributes.addFlashAttribute("ok", "Asistencia guardada correctamente");
        } catch (ReglaNegocioException | RecursoNoEncontradoException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        String redirect = "redirect:/asistencia";
        if (form.getSeccionId() != null && form.getFecha() != null) {
            redirect += "?seccionId=" + form.getSeccionId() + "&fecha=" + form.getFecha();
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
