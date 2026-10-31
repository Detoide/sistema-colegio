package com.primeschool.matricula.service;

import com.primeschool.matricula.dto.AsistenciaFilaDTO;
import com.primeschool.matricula.dto.AsistenciaForm;
import com.primeschool.matricula.model.entity.Usuario;
import com.primeschool.matricula.model.enums.EstadoAsistencia;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Servicio de registro y consulta de asistencia.
 */
public interface AsistenciaService {

    /**
     * Construye la planilla de asistencia de una sección en una fecha.
     *
     * @param seccionId identificador de la sección
     * @param fecha fecha de la planilla
     * @param usuario usuario que consulta la planilla
     * @return filas con el estado de asistencia actual
     */
    List<AsistenciaFilaDTO> planilla(Integer seccionId, LocalDate fecha, Usuario usuario);

    /**
     * Guarda los estados de asistencia registrados en la planilla.
     *
     * @param form datos de la planilla
     * @param usuario usuario que registra la asistencia
     */
    void guardar(AsistenciaForm form, Usuario usuario);

    /**
     * Resume la asistencia de una matrícula en un rango de fechas.
     *
     * @param matriculaId identificador de la matrícula
     * @param desde fecha inicial del rango
     * @param hasta fecha final del rango
     * @return cantidad de registros por estado
     */
    Map<EstadoAsistencia, Long> resumen(Integer matriculaId, LocalDate desde, LocalDate hasta);
}
