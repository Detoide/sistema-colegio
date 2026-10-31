package com.primeschool.matricula.service;

import com.primeschool.matricula.dto.BoletaDTO;
import com.primeschool.matricula.model.entity.Usuario;

/**
 * Servicio de generación de boletas de notas y asistencia.
 */
public interface BoletaService {

    /**
     * Genera la boleta de una matrícula para un periodo.
     *
     * @param idMatricula identificador de la matrícula
     * @param idPeriodo identificador del periodo
     * @param usuario usuario que solicita la boleta
     * @return datos consolidados de la boleta
     */
    BoletaDTO generar(Integer idMatricula, Integer idPeriodo, Usuario usuario);
}
