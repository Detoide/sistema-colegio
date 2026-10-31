package com.primeschool.matricula.service;

import com.primeschool.matricula.model.entity.PeriodoAcademico;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio de gestión de periodos académicos.
 */
public interface PeriodoService {

    /**
     * Lista todos los periodos académicos.
     *
     * @return periodos registrados
     */
    List<PeriodoAcademico> listar();

    /**
     * Obtiene un periodo por su identificador.
     *
     * @param id identificador del periodo
     * @return periodo encontrado
     */
    PeriodoAcademico obtener(Integer id);

    /**
     * Busca el periodo anual que contiene una fecha.
     *
     * @param fecha fecha a evaluar
     * @return periodo anual que contiene la fecha
     */
    PeriodoAcademico periodoAnualQueContiene(LocalDate fecha);

    /**
     * Obtiene el periodo anual que contiene a otro periodo.
     *
     * @param periodo periodo de referencia
     * @return periodo anual contenedor
     */
    PeriodoAcademico periodoAnualPara(PeriodoAcademico periodo);

    /**
     * Indica si un periodo corresponde a un año académico completo.
     *
     * @param periodo periodo a evaluar
     * @return {@code true} si el periodo es anual
     */
    boolean esAnual(PeriodoAcademico periodo);

    /**
     * Crea un periodo académico con las fechas indicadas.
     *
     * @param nombre nombre del periodo
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     */
    void crear(String nombre, LocalDate fechaInicio, LocalDate fechaFin);
}
