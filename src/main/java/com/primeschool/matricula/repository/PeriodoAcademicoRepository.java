package com.primeschool.matricula.repository;

import com.primeschool.matricula.model.entity.PeriodoAcademico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio de acceso a los periodos académicos.
 */
public interface PeriodoAcademicoRepository extends JpaRepository<PeriodoAcademico, Integer> {

    /**
     * Lista los periodos ordenados por fecha de inicio.
     *
     * @return periodos ordenados cronológicamente
     */
    List<PeriodoAcademico> findAllByOrderByFechaInicioAsc();

    /**
     * Comprueba si existe un periodo con el nombre indicado.
     *
     * @param nombrePeriodo nombre del periodo
     * @return {@code true} si el nombre ya está registrado
     */
    boolean existsByNombrePeriodo(String nombrePeriodo);
}
