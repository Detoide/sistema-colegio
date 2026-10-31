package com.primeschool.matricula.service.impl;

import com.primeschool.matricula.dto.AsistenciaFilaDTO;
import com.primeschool.matricula.dto.AsistenciaForm;
import com.primeschool.matricula.exception.RecursoNoEncontradoException;
import com.primeschool.matricula.exception.ReglaNegocioException;
import com.primeschool.matricula.model.entity.Asistencia;
import com.primeschool.matricula.model.entity.Matricula;
import com.primeschool.matricula.model.entity.PeriodoAcademico;
import com.primeschool.matricula.model.entity.Seccion;
import com.primeschool.matricula.model.entity.Usuario;
import com.primeschool.matricula.model.enums.EstadoAsistencia;
import com.primeschool.matricula.model.enums.EstadoMatricula;
import com.primeschool.matricula.repository.AsistenciaRepository;
import com.primeschool.matricula.repository.MatriculaRepository;
import com.primeschool.matricula.repository.SeccionRepository;
import com.primeschool.matricula.service.AlertaService;
import com.primeschool.matricula.service.AsistenciaService;
import com.primeschool.matricula.service.PeriodoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de asistencia.
 *
 * <p>Valida el acceso del docente tutor y actualiza las alertas de
 * inasistencia al guardar los estados de asistencia.</p>
 */
@Service
public class AsistenciaServiceImpl implements AsistenciaService {

    private static final List<EstadoMatricula> ESTADOS_ACTIVOS =
            List.of(EstadoMatricula.PENDIENTE, EstadoMatricula.CONFIRMADA);

    private final AsistenciaRepository asistenciaRepository;
    private final MatriculaRepository matriculaRepository;
    private final SeccionRepository seccionRepository;
    private final PeriodoService periodoService;
    private final AlertaService alertaService;

    /**
     * Crea el servicio con sus repositorios y servicios de apoyo.
     *
     * @param asistenciaRepository repositorio de asistencias
     * @param matriculaRepository repositorio de matrículas
     * @param seccionRepository repositorio de secciones
     * @param periodoService servicio de periodos
     * @param alertaService servicio de alertas
     */
    public AsistenciaServiceImpl(AsistenciaRepository asistenciaRepository,
                                 MatriculaRepository matriculaRepository,
                                 SeccionRepository seccionRepository,
                                 PeriodoService periodoService,
                                 AlertaService alertaService) {
        this.asistenciaRepository = asistenciaRepository;
        this.matriculaRepository = matriculaRepository;
        this.seccionRepository = seccionRepository;
        this.periodoService = periodoService;
        this.alertaService = alertaService;
    }

    /**
     * {@inheritDoc}
     *
     * @throws RecursoNoEncontradoException si la sección no existe
     * @throws ReglaNegocioException si el usuario no puede consultar la planilla
     */
    @Override
    public List<AsistenciaFilaDTO> planilla(Integer seccionId, LocalDate fecha, Usuario usuario) {
        if (seccionId == null || fecha == null) {
            return List.of();
        }
        Seccion seccion = seccionRepository.findById(seccionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sección no encontrada"));
        validarLectura(seccion, usuario);
        PeriodoAcademico anual = periodoService.periodoAnualQueContiene(fecha);

        List<Matricula> matriculas = matriculaRepository
                .findBySeccion_IdSeccionAndPeriodo_IdPeriodoAndEstadoInOrderByEstudiante_ApellidosAsc(
                        seccionId, anual.getIdPeriodo(), ESTADOS_ACTIVOS);

        return matriculas.stream()
                .map(m -> {
                    Asistencia asistencia = asistenciaRepository
                            .findByMatricula_IdMatriculaAndFecha(m.getIdMatricula(), fecha)
                            .orElse(null);
                    return new AsistenciaFilaDTO(
                            m.getIdMatricula(),
                            m.getEstudiante().getCodigo(),
                            m.getEstudiante().getNombreCompleto(),
                            asistencia != null ? asistencia.getEstado() : null);
                })
                .toList();
    }

    /**
     * {@inheritDoc}
     *
     * @throws ReglaNegocioException si el usuario, el estado o la matrícula no son válidos
     * @throws RecursoNoEncontradoException si la sección no existe
     */
    @Override
    @Transactional
    public void guardar(AsistenciaForm form, Usuario usuario) {
        if (form == null || form.getSeccionId() == null || form.getFecha() == null) {
            throw new ReglaNegocioException("Debe seleccionar sección y fecha");
        }
        if (!"DOCENTE".equalsIgnoreCase(usuario.getRol().getNombreRol())) {
            throw new ReglaNegocioException("Solo el docente tutor puede registrar asistencia");
        }

        Seccion seccion = seccionRepository.findById(form.getSeccionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Sección no encontrada"));
        validarDocenteTutor(seccion, usuario);
        PeriodoAcademico anual = periodoService.periodoAnualQueContiene(form.getFecha());

        Map<Integer, Matricula> matriculasActivas = matriculaRepository
                .findBySeccion_IdSeccionAndPeriodo_IdPeriodoAndEstadoInOrderByEstudiante_ApellidosAsc(
                        seccion.getIdSeccion(), anual.getIdPeriodo(), ESTADOS_ACTIVOS)
                .stream()
                .collect(Collectors.toMap(Matricula::getIdMatricula, Function.identity()));

        if (form.getEstados() == null) {
            return;
        }
        for (Map.Entry<Integer, String> entry : form.getEstados().entrySet()) {
            String valor = entry.getValue();
            if (valor == null || valor.isBlank()) {
                continue;
            }
            EstadoAsistencia estado;
            try {
                estado = EstadoAsistencia.valueOf(valor.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new ReglaNegocioException("Estado de asistencia inválido: " + valor);
            }
            Matricula matricula = matriculasActivas.get(entry.getKey());
            if (matricula == null) {
                throw new ReglaNegocioException("La matrícula no pertenece a la sección o no está vigente");
            }
            Asistencia asistencia = asistenciaRepository
                    .findByMatricula_IdMatriculaAndFecha(matricula.getIdMatricula(), form.getFecha())
                    .orElseGet(Asistencia::new);
            asistencia.setMatricula(matricula);
            asistencia.setFecha(form.getFecha());
            asistencia.setEstado(estado);
            asistenciaRepository.save(asistencia);

            alertaService.evaluarInasistencia(matricula.getIdMatricula());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<EstadoAsistencia, Long> resumen(Integer matriculaId, LocalDate desde, LocalDate hasta) {
        Map<EstadoAsistencia, Long> resumen = new EnumMap<>(EstadoAsistencia.class);
        for (EstadoAsistencia estado : EstadoAsistencia.values()) {
            resumen.put(estado, 0L);
        }
        if (matriculaId == null || desde == null || hasta == null) {
            return resumen;
        }
        for (Asistencia asistencia : asistenciaRepository
                .findByMatricula_IdMatriculaAndFechaBetween(matriculaId, desde, hasta)) {
            resumen.merge(asistencia.getEstado(), 1L, Long::sum);
        }
        return resumen;
    }

    private void validarLectura(Seccion seccion, Usuario usuario) {
        String rol = usuario.getRol().getNombreRol().toUpperCase();
        if ("APODERADO".equals(rol)) {
            throw new ReglaNegocioException("No tiene acceso a la planilla de asistencia");
        }
        if ("DOCENTE".equals(rol)) {
            validarDocenteTutor(seccion, usuario);
        }
    }

    private void validarDocenteTutor(Seccion seccion, Usuario usuario) {
        if (seccion.getTutor() == null
                || !seccion.getTutor().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new ReglaNegocioException("No es el docente tutor de esta sección");
        }
    }
}
