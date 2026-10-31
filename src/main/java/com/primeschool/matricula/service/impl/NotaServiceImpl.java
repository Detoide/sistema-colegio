package com.primeschool.matricula.service.impl;

import com.primeschool.matricula.dto.NotaFilaDTO;
import com.primeschool.matricula.dto.NotaForm;
import com.primeschool.matricula.exception.RecursoNoEncontradoException;
import com.primeschool.matricula.exception.ReglaNegocioException;
import com.primeschool.matricula.model.entity.Curso;
import com.primeschool.matricula.model.entity.Matricula;
import com.primeschool.matricula.model.entity.Nota;
import com.primeschool.matricula.model.entity.PeriodoAcademico;
import com.primeschool.matricula.model.entity.Seccion;
import com.primeschool.matricula.model.entity.Usuario;
import com.primeschool.matricula.model.enums.EstadoMatricula;
import com.primeschool.matricula.repository.CursoRepository;
import com.primeschool.matricula.repository.MatriculaRepository;
import com.primeschool.matricula.repository.NotaRepository;
import com.primeschool.matricula.repository.PeriodoAcademicoRepository;
import com.primeschool.matricula.repository.SeccionRepository;
import com.primeschool.matricula.service.AlertaService;
import com.primeschool.matricula.service.NotaService;
import com.primeschool.matricula.service.PeriodoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de calificaciones.
 *
 * <p>Valida el acceso del docente, el rango de las notas y actualiza las
 * alertas de riesgo académico al guardar una calificación.</p>
 */
@Service
public class NotaServiceImpl implements NotaService {

    private static final List<EstadoMatricula> ESTADOS_ACTIVOS =
            List.of(EstadoMatricula.PENDIENTE, EstadoMatricula.CONFIRMADA);
    private static final BigDecimal NOTA_MINIMA = BigDecimal.ZERO;
    private static final BigDecimal NOTA_MAXIMA = new BigDecimal("20");

    private final NotaRepository notaRepository;
    private final MatriculaRepository matriculaRepository;
    private final CursoRepository cursoRepository;
    private final SeccionRepository seccionRepository;
    private final PeriodoAcademicoRepository periodoRepository;
    private final PeriodoService periodoService;
    private final AlertaService alertaService;

    /**
     * Crea el servicio con sus repositorios y servicios de apoyo.
     *
     * @param notaRepository repositorio de notas
     * @param matriculaRepository repositorio de matrículas
     * @param cursoRepository repositorio de cursos
     * @param seccionRepository repositorio de secciones
     * @param periodoRepository repositorio de periodos
     * @param periodoService servicio de periodos
     * @param alertaService servicio de alertas
     */
    public NotaServiceImpl(NotaRepository notaRepository,
                           MatriculaRepository matriculaRepository,
                           CursoRepository cursoRepository,
                           SeccionRepository seccionRepository,
                           PeriodoAcademicoRepository periodoRepository,
                           PeriodoService periodoService,
                           AlertaService alertaService) {
        this.notaRepository = notaRepository;
        this.matriculaRepository = matriculaRepository;
        this.cursoRepository = cursoRepository;
        this.seccionRepository = seccionRepository;
        this.periodoRepository = periodoRepository;
        this.periodoService = periodoService;
        this.alertaService = alertaService;
    }

    /**
     * {@inheritDoc}
     *
     * @throws RecursoNoEncontradoException si la sección o el periodo no existen
     * @throws ReglaNegocioException si el usuario no puede consultar la planilla
     */
    @Override
    public List<NotaFilaDTO> planilla(Integer seccionId, Integer periodoId, Integer cursoId, Usuario usuario) {
        if (seccionId == null || periodoId == null || cursoId == null) {
            return List.of();
        }
        Seccion seccion = seccionRepository.findById(seccionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sección no encontrada"));
        PeriodoAcademico periodo = periodoRepository.findById(periodoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Periodo no encontrado"));
        validarLectura(seccion, usuario);
        PeriodoAcademico anual = periodoService.periodoAnualPara(periodo);

        List<Matricula> matriculas = matriculaRepository
                .findBySeccion_IdSeccionAndPeriodo_IdPeriodoAndEstadoInOrderByEstudiante_ApellidosAsc(
                        seccionId, anual.getIdPeriodo(), ESTADOS_ACTIVOS);

        return matriculas.stream()
                .map(m -> {
                    Nota nota = notaRepository
                            .findByMatricula_IdMatriculaAndCurso_IdCursoAndPeriodo_IdPeriodo(
                                    m.getIdMatricula(), cursoId, periodoId)
                            .orElse(null);
                    return new NotaFilaDTO(
                            m.getIdMatricula(),
                            m.getEstudiante().getCodigo(),
                            m.getEstudiante().getNombreCompleto(),
                            nota != null ? nota.getCalificacion() : null);
                })
                .toList();
    }

    /**
     * {@inheritDoc}
     *
     * @throws ReglaNegocioException si el usuario, la nota o la matrícula no son válidos
     * @throws RecursoNoEncontradoException si la sección, el periodo o el curso no existen
     */
    @Override
    @Transactional
    public void guardar(NotaForm form, Usuario usuario) {
        if (form == null || form.getSeccionId() == null || form.getPeriodoId() == null
                || form.getCursoId() == null) {
            throw new ReglaNegocioException("Debe seleccionar sección, periodo y curso");
        }
        if (!"DOCENTE".equalsIgnoreCase(usuario.getRol().getNombreRol())) {
            throw new ReglaNegocioException("Solo el docente tutor puede registrar notas");
        }

        Seccion seccion = seccionRepository.findById(form.getSeccionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Sección no encontrada"));
        validarDocenteTutor(seccion, usuario);

        PeriodoAcademico periodo = periodoRepository.findById(form.getPeriodoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Periodo no encontrado"));
        PeriodoAcademico anual = periodoService.periodoAnualPara(periodo);
        Curso curso = cursoRepository.findById(form.getCursoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Curso no encontrado"));

        Map<Integer, Matricula> matriculasActivas = matriculaRepository
                .findBySeccion_IdSeccionAndPeriodo_IdPeriodoAndEstadoInOrderByEstudiante_ApellidosAsc(
                        seccion.getIdSeccion(), anual.getIdPeriodo(), ESTADOS_ACTIVOS)
                .stream()
                .collect(Collectors.toMap(Matricula::getIdMatricula, Function.identity()));

        if (form.getNotas() == null) {
            return;
        }
        for (Map.Entry<Integer, BigDecimal> entry : form.getNotas().entrySet()) {
            BigDecimal valor = entry.getValue();
            if (valor == null) {
                continue;
            }
            if (valor.compareTo(NOTA_MINIMA) < 0 || valor.compareTo(NOTA_MAXIMA) > 0) {
                throw new ReglaNegocioException("La calificación debe estar entre 0 y 20");
            }
            Matricula matricula = matriculasActivas.get(entry.getKey());
            if (matricula == null) {
                throw new ReglaNegocioException("La matrícula no pertenece a la sección o no está vigente");
            }
            Nota nota = notaRepository
                    .findByMatricula_IdMatriculaAndCurso_IdCursoAndPeriodo_IdPeriodo(
                            matricula.getIdMatricula(), curso.getIdCurso(), periodo.getIdPeriodo())
                    .orElseGet(Nota::new);
            nota.setMatricula(matricula);
            nota.setCurso(curso);
            nota.setPeriodo(periodo);
            nota.setDocente(usuario);
            nota.setCalificacion(valor);
            notaRepository.save(nota);

            alertaService.evaluarRiesgoAcademico(matricula.getIdMatricula(), periodo.getIdPeriodo());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal promedio(Integer matriculaId, Integer periodoId) {
        Double promedio = notaRepository.promedio(matriculaId, periodoId);
        return promedio == null ? null : BigDecimal.valueOf(promedio).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Nota> notasPorMatriculaYPeriodo(Integer matriculaId, Integer periodoId) {
        return notaRepository.findByMatricula_IdMatriculaAndPeriodo_IdPeriodo(matriculaId, periodoId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Nota> notasPorMatricula(Integer matriculaId) {
        return notaRepository.findByMatricula_IdMatriculaOrderByPeriodo_FechaInicioAscCurso_NombreCursoAsc(
                matriculaId);
    }

    private void validarLectura(Seccion seccion, Usuario usuario) {
        String rol = usuario.getRol().getNombreRol().toUpperCase();
        if ("APODERADO".equals(rol)) {
            throw new ReglaNegocioException("No tiene acceso a la planilla de notas");
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
