package com.primeschool.matricula.repository;

import com.primeschool.matricula.model.entity.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de acceso a las calificaciones registradas.
 */
public interface NotaRepository extends JpaRepository<Nota, Integer> {

    /**
     * Lista las notas de una matrícula en un periodo.
     *
     * @param idMatricula identificador de la matrícula
     * @param idPeriodo identificador del periodo
     * @return notas encontradas
     */
    List<Nota> findByMatricula_IdMatriculaAndPeriodo_IdPeriodo(
            Integer idMatricula, Integer idPeriodo);

    /**
     * Busca la nota de una matrícula en un curso y periodo.
     *
     * @param idMatricula identificador de la matrícula
     * @param idCurso identificador del curso
     * @param idPeriodo identificador del periodo
     * @return nota encontrada, si existe
     */
    Optional<Nota> findByMatricula_IdMatriculaAndCurso_IdCursoAndPeriodo_IdPeriodo(
            Integer idMatricula, Integer idCurso, Integer idPeriodo);

    /**
     * Lista las notas de una sección en un periodo.
     *
     * @param idSeccion identificador de la sección
     * @param idPeriodo identificador del periodo
     * @return notas de la sección
     */
    List<Nota> findByMatricula_Seccion_IdSeccionAndPeriodo_IdPeriodo(
            Integer idSeccion, Integer idPeriodo);

    /**
     * Lista el historial de notas de una matrícula.
     *
     * @param idMatricula identificador de la matrícula
     * @return notas ordenadas por periodo y curso
     */
    List<Nota> findByMatricula_IdMatriculaOrderByPeriodo_FechaInicioAscCurso_NombreCursoAsc(
            Integer idMatricula);

    /**
     * Calcula el promedio de notas de una matrícula en un periodo.
     *
     * @param idMatricula identificador de la matrícula
     * @param idPeriodo identificador del periodo
     * @return promedio calculado o {@code null} si no hay notas
     */
    @Query("""
            select avg(n.calificacion)
            from Nota n
            where n.matricula.idMatricula = :idMatricula
              and n.periodo.idPeriodo = :idPeriodo
            """)
    Double promedio(@Param("idMatricula") Integer idMatricula,
                    @Param("idPeriodo") Integer idPeriodo);
}
