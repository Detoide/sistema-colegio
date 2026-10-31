package com.primeschool.matricula.repository;

import com.primeschool.matricula.model.entity.Asistencia;
import com.primeschool.matricula.model.enums.EstadoAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de acceso a los registros de asistencia.
 */
public interface AsistenciaRepository extends JpaRepository<Asistencia, Integer> {

    /**
     * Busca la asistencia de una matrícula en una fecha.
     *
     * @param idMatricula identificador de la matrícula
     * @param fecha fecha de asistencia
     * @return registro encontrado, si existe
     */
    Optional<Asistencia> findByMatricula_IdMatriculaAndFecha(Integer idMatricula, LocalDate fecha);

    /**
     * Lista la asistencia de una sección en una fecha.
     *
     * @param idSeccion identificador de la sección
     * @param fecha fecha de asistencia
     * @return registros ordenados por estudiante
     */
    List<Asistencia> findByMatricula_Seccion_IdSeccionAndFechaOrderByMatricula_Estudiante_ApellidosAsc(
            Integer idSeccion, LocalDate fecha);

    /**
     * Lista la asistencia de una matrícula dentro de un rango de fechas.
     *
     * @param idMatricula identificador de la matrícula
     * @param desde fecha inicial del rango
     * @param hasta fecha final del rango
     * @return registros dentro del rango
     */
    List<Asistencia> findByMatricula_IdMatriculaAndFechaBetween(
            Integer idMatricula, LocalDate desde, LocalDate hasta);

    /**
     * Cuenta las asistencias de una matrícula con el estado indicado.
     *
     * @param idMatricula identificador de la matrícula
     * @param estado estado de asistencia
     * @return cantidad de registros con ese estado
     */
    long countByMatricula_IdMatriculaAndEstado(Integer idMatricula, EstadoAsistencia estado);
}
