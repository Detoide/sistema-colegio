package com.primeschool.matricula.repository;

import com.primeschool.matricula.model.entity.Matricula;
import com.primeschool.matricula.model.enums.EstadoMatricula;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de acceso a las matrículas.
 */
public interface MatriculaRepository extends JpaRepository<Matricula, Integer> {

    /**
     * Lista todas las matrículas ordenadas por periodo y estudiante.
     *
     * @return matrículas ordenadas cronológicamente
     */
    List<Matricula> findAllByOrderByPeriodo_FechaInicioDescEstudiante_ApellidosAsc();

    /**
     * Lista las matrículas de un periodo.
     *
     * @param idPeriodo identificador del periodo
     * @return matrículas del periodo
     */
    List<Matricula> findByPeriodo_IdPeriodoOrderByEstudiante_ApellidosAsc(Integer idPeriodo);

    /**
     * Lista las matrículas de una sección en un periodo.
     *
     * @param idSeccion identificador de la sección
     * @param idPeriodo identificador del periodo
     * @return matrículas de la sección
     */
    List<Matricula> findBySeccion_IdSeccionAndPeriodo_IdPeriodoOrderByEstudiante_ApellidosAsc(
            Integer idSeccion, Integer idPeriodo);

    /**
     * Lista las matrículas de una sección y periodo con los estados indicados.
     *
     * @param idSeccion identificador de la sección
     * @param idPeriodo identificador del periodo
     * @param estados estados de matrícula a incluir
     * @return matrículas que cumplen el filtro
     */
    List<Matricula> findBySeccion_IdSeccionAndPeriodo_IdPeriodoAndEstadoInOrderByEstudiante_ApellidosAsc(
            Integer idSeccion, Integer idPeriodo, Collection<EstadoMatricula> estados);

    /**
     * Busca la matrícula de un estudiante en un periodo.
     *
     * @param idEstudiante identificador del estudiante
     * @param idPeriodo identificador del periodo
     * @return matrícula encontrada, si existe
     */
    Optional<Matricula> findByEstudiante_IdEstudianteAndPeriodo_IdPeriodo(
            Integer idEstudiante, Integer idPeriodo);

    /**
     * Lista las matrículas de las secciones asignadas a un docente.
     *
     * @param idUsuario identificador del usuario tutor
     * @return matrículas de las secciones del docente
     */
    List<Matricula> findBySeccion_Tutor_IdUsuarioOrderByPeriodo_FechaInicioDescEstudiante_ApellidosAsc(
            Integer idUsuario);

    /**
     * Lista las matrículas de los estudiantes a cargo de un apoderado.
     *
     * @param idUsuario identificador del usuario apoderado
     * @return matrículas de los estudiantes del apoderado
     */
    List<Matricula> findByEstudiante_Apoderado_Usuario_IdUsuarioOrderByPeriodo_FechaInicioDescEstudiante_ApellidosAsc(
            Integer idUsuario);

    /**
     * Cuenta las matrículas de una sección con los estados indicados.
     *
     * @param idSeccion identificador de la sección
     * @param estados estados de matrícula a contar
     * @return cantidad de matrículas que cumplen el filtro
     */
    long countBySeccion_IdSeccionAndEstadoIn(Integer idSeccion, Collection<EstadoMatricula> estados);
}
