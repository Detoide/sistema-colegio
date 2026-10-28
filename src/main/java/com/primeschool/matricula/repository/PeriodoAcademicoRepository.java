package com.primeschool.matricula.repository;

import com.primeschool.matricula.model.entity.PeriodoAcademico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PeriodoAcademicoRepository extends JpaRepository<PeriodoAcademico, Integer> {

    List<PeriodoAcademico> findAllByOrderByFechaInicioAsc();

    boolean existsByNombrePeriodo(String nombrePeriodo);
}
