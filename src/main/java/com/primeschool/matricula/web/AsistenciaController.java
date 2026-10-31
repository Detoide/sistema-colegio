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

/**
 * Controlador de consulta y registro de asistencia.
 */
@Controller
public class AsistenciaController {

    private final AsistenciaService asistenciaService;
    private final CatalogoService catalogoService;
    private final UsuarioSesion usuarioSesion;

    /**
     * Crea el controlador con los servicios de asistencia y catálogos.
     *
     * @param asistenciaService servicio de asistencia
     * @param catalogoService servicio de catálogos
     * @param usuarioSesion accesor del usuario autenticado
     */
    public AsistenciaController(AsistenciaService asistenciaService,
                                CatalogoService catalogoService,
                                UsuarioSesion usuarioSesion) {
        this.asistenciaService = asistenciaService;
        this.catalogoService = catalogoService;
        this.usuarioSesion = usuarioSesion;
    }

    /**
     * Muestra la planilla de asistencia de una sección en una fecha.
     *
     * @param seccionId identificador de la sección
     * @param fecha fecha de la planilla
     * @param authentication autenticación de Spring Security
     * @param model modelo de la vista
     * @return nombre de la vista de asistencia
     */
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

    /**
     * Guarda los estados de asistencia de la planilla.
     *
     * @param form datos de la planilla
     * @param authentication autenticación de Spring Security
     * @param redirectAttributes atributos para la redirección
     * @return redirección a la planilla con los filtros aplicados
     */
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
