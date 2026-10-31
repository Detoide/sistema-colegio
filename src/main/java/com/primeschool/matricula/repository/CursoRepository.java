package com.primeschool.matricula.repository;

import com.primeschool.matricula.model.entity.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio de acceso a los cursos.
 */
public interface CursoRepository extends JpaRepository<Curso, Integer> {

    /**
     * Lista los cursos ordenados por nombre.
     *
     * @return cursos ordenados alfabéticamente
     */
    List<Curso> findAllByOrderByNombreCursoAsc();

    /**
     * Comprueba si existe un curso con el nombre indicado.
     *
     * @param nombreCurso nombre del curso
     * @return {@code true} si el curso ya está registrado
     */
    boolean existsByNombreCurso(String nombreCurso);
}
