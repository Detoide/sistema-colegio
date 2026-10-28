package com.primeschool.matricula.repository;

import com.primeschool.matricula.model.entity.Asistencia;
import com.primeschool.matricula.model.enums.EstadoAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Integer> {

    Optional<Asistencia> findByMatricula_IdMatriculaAndFecha(Integer idMatricula, LocalDate fecha);

    List<Asistencia> findByMatricula_Seccion_IdSeccionAndFechaOrderByMatricula_Estudiante_ApellidosAsc(
            Integer idSeccion, LocalDate fecha);

    List<Asistencia> findByMatricula_IdMatriculaAndFechaBetween(
            Integer idMatricula, LocalDate desde, LocalDate hasta);

    long countByMatricula_IdMatriculaAndEstado(Integer idMatricula, EstadoAsistencia estado);
}
