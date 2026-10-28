package com.primeschool.matricula.repository;

import com.primeschool.matricula.model.entity.Grado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradoRepository extends JpaRepository<Grado, Integer> {

    List<Grado> findAllByOrderByNivelAscNombreGradoAsc();

    boolean existsByNombreGradoAndNivel(String nombreGrado, String nivel);
}
