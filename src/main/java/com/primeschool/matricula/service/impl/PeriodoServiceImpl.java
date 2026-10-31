package com.primeschool.matricula.service.impl;

import com.primeschool.matricula.exception.RecursoNoEncontradoException;
import com.primeschool.matricula.exception.ReglaNegocioException;
import com.primeschool.matricula.model.entity.PeriodoAcademico;
import com.primeschool.matricula.repository.PeriodoAcademicoRepository;
import com.primeschool.matricula.service.PeriodoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * Implementación del servicio de periodos académicos.
 */
@Service
public class PeriodoServiceImpl implements PeriodoService {

    private final PeriodoAcademicoRepository periodoRepository;

    /**
     * Crea el servicio con el repositorio de periodos.
     *
     * @param periodoRepository repositorio de periodos
     */
    public PeriodoServiceImpl(PeriodoAcademicoRepository periodoRepository) {
        this.periodoRepository = periodoRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PeriodoAcademico> listar() {
        return periodoRepository.findAllByOrderByFechaInicioAsc();
    }

    /**
     * {@inheritDoc}
     *
     * @throws RecursoNoEncontradoException si el periodo no existe
     */
    @Override
    public PeriodoAcademico obtener(Integer id) {
        return periodoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Periodo no encontrado"));
    }

    /**
     * {@inheritDoc}
     *
     * @throws ReglaNegocioException si ningún periodo contiene la fecha
     */
    @Override
    public PeriodoAcademico periodoAnualQueContiene(LocalDate fecha) {
        return periodoRepository.findAllByOrderByFechaInicioAsc().stream()
                .filter(p -> p.contiene(fecha))
                .max(Comparator.comparingLong(p -> java.time.temporal.ChronoUnit.DAYS.between(p.getFechaInicio(), p.getFechaFin())))
                .orElseThrow(() -> new ReglaNegocioException(
                        "No existe un periodo académico que contenga la fecha " + fecha));
    }

    /**
     * {@inheritDoc}
     *
     * @throws ReglaNegocioException si el periodo no pertenece a uno anual
     */
    @Override
    public PeriodoAcademico periodoAnualPara(PeriodoAcademico periodo) {
        if (periodo == null) {
            throw new ReglaNegocioException("Debe seleccionar un periodo");
        }
        return periodoRepository.findAllByOrderByFechaInicioAsc().stream()
                .filter(p -> !p.getIdPeriodo().equals(periodo.getIdPeriodo()))
                .filter(p -> p.contiene(periodo))
                .max(Comparator.comparingLong(p -> java.time.temporal.ChronoUnit.DAYS.between(p.getFechaInicio(), p.getFechaFin())))
                .orElseThrow(() -> new ReglaNegocioException(
                        "El periodo seleccionado no pertenece a un periodo anual"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean esAnual(PeriodoAcademico periodo) {
        if (periodo == null) {
            return false;
        }
        return periodoRepository.findAllByOrderByFechaInicioAsc().stream()
                .noneMatch(p -> !p.getIdPeriodo().equals(periodo.getIdPeriodo()) && p.contiene(periodo));
    }

    /**
     * {@inheritDoc}
     *
     * @throws ReglaNegocioException si los datos del periodo no son válidos
     */
    @Override
    @Transactional
    public void crear(String nombre, LocalDate fechaInicio, LocalDate fechaFin) {
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaNegocioException("El nombre del periodo es obligatorio");
        }
        if (fechaInicio == null || fechaFin == null || !fechaFin.isAfter(fechaInicio)) {
            throw new ReglaNegocioException("La fecha fin debe ser posterior a la fecha de inicio");
        }
        if (periodoRepository.existsByNombrePeriodo(nombre)) {
            throw new ReglaNegocioException("Ya existe un periodo con ese nombre");
        }
        PeriodoAcademico periodo = new PeriodoAcademico();
        periodo.setNombrePeriodo(nombre.trim());
        periodo.setFechaInicio(fechaInicio);
        periodo.setFechaFin(fechaFin);
        periodoRepository.save(periodo);
    }
}
