package com.primeschool.matricula.repository;

import com.primeschool.matricula.model.entity.AlertaTemprana;
import com.primeschool.matricula.model.enums.TipoRiesgo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertaTempranaRepository extends JpaRepository<AlertaTemprana, Integer> {

    List<AlertaTemprana> findByAtendidaFalseOrderByFechaGeneradaDesc();

    List<AlertaTemprana> findAllByOrderByFechaGeneradaDesc();

    List<AlertaTemprana> findByMatricula_Seccion_Tutor_IdUsuarioAndAtendidaFalseOrderByFechaGeneradaDesc(
            Integer idUsuario);

    List<AlertaTemprana> findByMatricula_Estudiante_Apoderado_Usuario_IdUsuarioOrderByFechaGeneradaDesc(
            Integer idUsuario);

    boolean existsByMatricula_IdMatriculaAndTipoRiesgoAndAtendidaFalse(
            Integer idMatricula, TipoRiesgo tipoRiesgo);

    long countByAtendidaFalse();
}
