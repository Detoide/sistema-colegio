package com.primeschool.matricula.repository;

import com.primeschool.matricula.model.entity.Matricula;
import com.primeschool.matricula.model.enums.EstadoMatricula;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MatriculaRepository extends JpaRepository<Matricula, Integer> {

    List<Matricula> findAllByOrderByPeriodo_FechaInicioDescEstudiante_ApellidosAsc();

    List<Matricula> findByPeriodo_IdPeriodoOrderByEstudiante_ApellidosAsc(Integer idPeriodo);

    List<Matricula> findBySeccion_IdSeccionAndPeriodo_IdPeriodoOrderByEstudiante_ApellidosAsc(
            Integer idSeccion, Integer idPeriodo);

    List<Matricula> findBySeccion_IdSeccionAndPeriodo_IdPeriodoAndEstadoInOrderByEstudiante_ApellidosAsc(
            Integer idSeccion, Integer idPeriodo, Collection<EstadoMatricula> estados);

    Optional<Matricula> findByEstudiante_IdEstudianteAndPeriodo_IdPeriodo(
            Integer idEstudiante, Integer idPeriodo);

    List<Matricula> findBySeccion_Tutor_IdUsuarioOrderByPeriodo_FechaInicioDescEstudiante_ApellidosAsc(
            Integer idUsuario);

    List<Matricula> findByEstudiante_Apoderado_Usuario_IdUsuarioOrderByPeriodo_FechaInicioDescEstudiante_ApellidosAsc(
            Integer idUsuario);

    long countBySeccion_IdSeccionAndEstadoIn( Integer idSeccion, Collection<EstadoMatricula> estados);
}
