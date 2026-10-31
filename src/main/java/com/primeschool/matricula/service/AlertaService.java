package com.primeschool.matricula.service;

import com.primeschool.matricula.model.entity.AlertaTemprana;
import com.primeschool.matricula.model.entity.Usuario;

import java.util.List;

/**
 * Servicio de detección y gestión de alertas tempranas.
 */
public interface AlertaService {

    /**
     * Lista las alertas visibles para el usuario según su rol.
     *
     * @param usuario usuario que consulta las alertas
     * @return alertas visibles
     */
    List<AlertaTemprana> listarVisibles(Usuario usuario);

    /**
     * Marca una alerta como atendida.
     *
     * @param idAlerta identificador de la alerta
     * @param usuario usuario que atiende la alerta
     */
    void atender(Integer idAlerta, Usuario usuario);

    /**
     * Evalúa el riesgo académico de una matrícula en un periodo.
     *
     * @param idMatricula identificador de la matrícula
     * @param idPeriodo identificador del periodo
     */
    void evaluarRiesgoAcademico(Integer idMatricula, Integer idPeriodo);

    /**
     * Evalúa el riesgo de inasistencia de una matrícula.
     *
     * @param idMatricula identificador de la matrícula
     */
    void evaluarInasistencia(Integer idMatricula);

    /**
     * Cuenta las alertas que aún no han sido atendidas.
     *
     * @return cantidad de alertas activas
     */
    long contarActivas();
}
