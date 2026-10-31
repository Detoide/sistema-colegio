package com.primeschool.matricula.service;

import com.primeschool.matricula.dto.RegistroAcademicoFilaDTO;
import com.primeschool.matricula.dto.RegistroAcademicoForm;
import com.primeschool.matricula.model.entity.Usuario;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio que coordina el registro de notas y asistencia.
 */
public interface RegistroAcademicoService {

    /**
     * Construye la planilla de estudiantes de una sección para un curso y fecha.
     *
     * @param seccionId identificador de la sección
     * @param periodoId identificador del periodo
     * @param cursoId identificador del curso
     * @param fecha fecha de la planilla
     * @param usuario usuario que consulta la planilla
     * @return filas con la nota y asistencia actuales
     */
    List<RegistroAcademicoFilaDTO> planilla(Integer seccionId,
                                            Integer periodoId,
                                            Integer cursoId,
                                            LocalDate fecha,
                                            Usuario usuario);

    /**
     * Guarda las notas y asistencias registradas en la planilla.
     *
     * @param form datos de la planilla
     * @param usuario usuario que realiza el registro
     */
    void guardar(RegistroAcademicoForm form, Usuario usuario);
}
