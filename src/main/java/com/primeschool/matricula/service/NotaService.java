package com.primeschool.matricula.service;

import com.primeschool.matricula.dto.NotaFilaDTO;
import com.primeschool.matricula.dto.NotaForm;
import com.primeschool.matricula.model.entity.Nota;
import com.primeschool.matricula.model.entity.Usuario;

import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio de registro y consulta de calificaciones.
 */
public interface NotaService {

    /**
     * Construye la planilla de notas de una sección en un curso y periodo.
     *
     * @param seccionId identificador de la sección
     * @param periodoId identificador del periodo
     * @param cursoId identificador del curso
     * @param usuario usuario que consulta la planilla
     * @return filas con la nota actual de cada estudiante
     */
    List<NotaFilaDTO> planilla(Integer seccionId, Integer periodoId, Integer cursoId, Usuario usuario);

    /**
     * Guarda las notas registradas en la planilla.
     *
     * @param form datos de la planilla
     * @param usuario usuario que registra las notas
     */
    void guardar(NotaForm form, Usuario usuario);

    /**
     * Calcula el promedio de notas de una matrícula en un periodo.
     *
     * @param matriculaId identificador de la matrícula
     * @param periodoId identificador del periodo
     * @return promedio calculado
     */
    BigDecimal promedio(Integer matriculaId, Integer periodoId);

    /**
     * Lista las notas de una matrícula en un periodo.
     *
     * @param matriculaId identificador de la matrícula
     * @param periodoId identificador del periodo
     * @return notas del periodo
     */
    List<Nota> notasPorMatriculaYPeriodo(Integer matriculaId, Integer periodoId);

    /**
     * Lista todas las notas de una matrícula.
     *
     * @param matriculaId identificador de la matrícula
     * @return historial de notas
     */
    List<Nota> notasPorMatricula(Integer matriculaId);
}
