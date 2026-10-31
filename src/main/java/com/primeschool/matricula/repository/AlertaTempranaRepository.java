package com.primeschool.matricula.repository;

import com.primeschool.matricula.model.entity.AlertaTemprana;
import com.primeschool.matricula.model.enums.TipoRiesgo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio de acceso a las alertas tempranas.
 */
public interface AlertaTempranaRepository extends JpaRepository<AlertaTemprana, Integer> {

    /**
     * Lista las alertas activas ordenadas por fecha de generación.
     *
     * @return alertas no atendidas, de la más reciente a la más antigua
     */
    List<AlertaTemprana> findByAtendidaFalseOrderByFechaGeneradaDesc();

    /**
     * Lista todas las alertas ordenadas por fecha de generación.
     *
     * @return alertas de la más reciente a la más antigua
     */
    List<AlertaTemprana> findAllByOrderByFechaGeneradaDesc();

    /**
     * Lista las alertas activas de las secciones asignadas a un docente.
     *
     * @param idUsuario identificador del usuario tutor
     * @return alertas de las secciones del docente
     */
    List<AlertaTemprana> findByMatricula_Seccion_Tutor_IdUsuarioAndAtendidaFalseOrderByFechaGeneradaDesc(
            Integer idUsuario);

    /**
     * Lista las alertas de los estudiantes a cargo de un apoderado.
     *
     * @param idUsuario identificador del usuario apoderado
     * @return alertas de los estudiantes del apoderado
     */
    List<AlertaTemprana> findByMatricula_Estudiante_Apoderado_Usuario_IdUsuarioOrderByFechaGeneradaDesc(
            Integer idUsuario);

    /**
     * Comprueba si existe una alerta activa para una matrícula y tipo de riesgo.
     *
     * @param idMatricula identificador de la matrícula
     * @param tipoRiesgo tipo de riesgo
     * @return {@code true} si ya existe una alerta activa
     */
    boolean existsByMatricula_IdMatriculaAndTipoRiesgoAndAtendidaFalse(
            Integer idMatricula, TipoRiesgo tipoRiesgo);

    /**
     * Cuenta las alertas que aún no han sido atendidas.
     *
     * @return cantidad de alertas activas
     */
    long countByAtendidaFalse();
}
