package com.primeschool.matricula.service.impl;

import com.primeschool.matricula.dto.BoletaDTO;
import com.primeschool.matricula.dto.BoletaItemDTO;
import com.primeschool.matricula.exception.RecursoNoEncontradoException;
import com.primeschool.matricula.exception.ReglaNegocioException;
import com.primeschool.matricula.model.entity.Matricula;
import com.primeschool.matricula.model.entity.Nota;
import com.primeschool.matricula.model.entity.PeriodoAcademico;
import com.primeschool.matricula.model.entity.Usuario;
import com.primeschool.matricula.model.enums.EstadoAsistencia;
import com.primeschool.matricula.repository.MatriculaRepository;
import com.primeschool.matricula.repository.NotaRepository;
import com.primeschool.matricula.repository.PeriodoAcademicoRepository;
import com.primeschool.matricula.service.AsistenciaService;
import com.primeschool.matricula.service.BoletaService;
import com.primeschool.matricula.service.PeriodoService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Implementación del servicio de generación de boletas.
 */
@Service
public class BoletaServiceImpl implements BoletaService {

    private final MatriculaRepository matriculaRepository;
    private final NotaRepository notaRepository;
    private final PeriodoAcademicoRepository periodoRepository;
    private final AsistenciaService asistenciaService;
    private final PeriodoService periodoService;

    /**
     * Crea el servicio con sus repositorios y servicios de apoyo.
     *
     * @param matriculaRepository repositorio de matrículas
     * @param notaRepository repositorio de notas
     * @param periodoRepository repositorio de periodos
     * @param asistenciaService servicio de asistencia
     * @param periodoService servicio de periodos
     */
    public BoletaServiceImpl(MatriculaRepository matriculaRepository,
                             NotaRepository notaRepository,
                             PeriodoAcademicoRepository periodoRepository,
                             AsistenciaService asistenciaService,
                             PeriodoService periodoService) {
        this.matriculaRepository = matriculaRepository;
        this.notaRepository = notaRepository;
        this.periodoRepository = periodoRepository;
        this.asistenciaService = asistenciaService;
        this.periodoService = periodoService;
    }

    /**
     * {@inheritDoc}
     *
     * @throws RecursoNoEncontradoException si la matrícula o el periodo no existen
     * @throws ReglaNegocioException si el usuario no puede ver la boleta o el periodo no es válido
     */
    @Override
    public BoletaDTO generar(Integer idMatricula, Integer idPeriodo, Usuario usuario) {
        Matricula matricula = matriculaRepository.findById(idMatricula)
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada"));
        if (!puedeVer(matricula, usuario)) {
            throw new ReglaNegocioException("No tiene acceso a la boleta de este estudiante");
        }
        PeriodoAcademico periodo = periodoRepository.findById(idPeriodo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Periodo no encontrado"));
        if (periodoService.esAnual(periodo)) {
            throw new ReglaNegocioException("La boleta se emite por bimestre, no por periodo anual");
        }
        PeriodoAcademico periodoAnual = periodoService.periodoAnualPara(periodo);
        if (!matricula.getPeriodo().getIdPeriodo().equals(periodoAnual.getIdPeriodo())) {
            throw new ReglaNegocioException("El bimestre seleccionado no pertenece al periodo de la matrícula");
        }

        BoletaDTO boleta = new BoletaDTO();
        boleta.setMatriculaId(matricula.getIdMatricula());
        boleta.setEstudiante(matricula.getEstudiante().getNombreCompleto());
        boleta.setCodigo(matricula.getEstudiante().getCodigo());
        boleta.setGrado(matricula.getSeccion().getGrado().getNombreGrado());
        boleta.setSeccion(matricula.getSeccion().getNombreSeccion());
        boleta.setPeriodo(periodo.getNombrePeriodo());

        List<Nota> notas = notaRepository.findByMatricula_IdMatriculaAndPeriodo_IdPeriodo(
                matricula.getIdMatricula(), periodo.getIdPeriodo());
        List<BoletaItemDTO> items = notas.stream()
                .sorted(Comparator.comparing(n -> n.getCurso().getNombreCurso()))
                .map(n -> new BoletaItemDTO(n.getCurso().getNombreCurso(), n.getCalificacion()))
                .toList();
        boleta.setNotas(items);
        boleta.setPromedio(promedio(notas));

        Map<EstadoAsistencia, Long> resumen = asistenciaService.resumen(
                matricula.getIdMatricula(), periodo.getFechaInicio(), periodo.getFechaFin());
        boleta.setAsistio(resumen.getOrDefault(EstadoAsistencia.ASISTIO, 0L));
        boleta.setFalta(resumen.getOrDefault(EstadoAsistencia.FALTA, 0L));
        boleta.setTardanza(resumen.getOrDefault(EstadoAsistencia.TARDANZA, 0L));
        boleta.setJustificada(resumen.getOrDefault(EstadoAsistencia.JUSTIFICADA, 0L));
        return boleta;
    }

    private BigDecimal promedio(List<Nota> notas) {
        if (notas.isEmpty()) {
            return null;
        }
        BigDecimal suma = notas.stream()
                .map(Nota::getCalificacion)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return suma.divide(BigDecimal.valueOf(notas.size()), 2, RoundingMode.HALF_UP);
    }

    private boolean puedeVer(Matricula matricula, Usuario usuario) {
        String rol = usuario.getRol().getNombreRol().toUpperCase();
        return switch (rol) {
            case "ADMINISTRADOR", "DIRECTIVO" -> true;
            case "DOCENTE" -> matricula.getSeccion().getTutor() != null
                    && matricula.getSeccion().getTutor().getIdUsuario().equals(usuario.getIdUsuario());
            case "APODERADO" -> matricula.getEstudiante().getApoderado() != null
                    && matricula.getEstudiante().getApoderado().getUsuario().getIdUsuario()
                    .equals(usuario.getIdUsuario());
            default -> false;
        };
    }
}
