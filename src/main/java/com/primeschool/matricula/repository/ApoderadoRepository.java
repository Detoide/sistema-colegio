package com.primeschool.matricula.repository;

import com.primeschool.matricula.model.entity.Apoderado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApoderadoRepository extends JpaRepository<Apoderado, Integer> {

    Optional<Apoderado> findByUsuario_IdUsuario(Integer idUsuario);

    Optional<Apoderado> findByDni(String dni);

    boolean existsByDni(String dni);

    List<Apoderado> findAllByOrderByUsuario_ApellidosAscUsuario_NombresAsc();
}
