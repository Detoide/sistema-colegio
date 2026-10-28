package com.primeschool.matricula.repository;

import com.primeschool.matricula.model.entity.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CursoRepository extends JpaRepository<Curso, Integer> {

    List<Curso> findAllByOrderByNombreCursoAsc();

    boolean existsByNombreCurso(String nombreCurso);
}
