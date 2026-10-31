package com.primeschool.matricula.repository;

import com.primeschool.matricula.model.entity.Grado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio de acceso a los grados académicos.
 */
public interface GradoRepository extends JpaRepository<Grado, Integer> {

    /**
     * Lista los grados ordenados por nivel y nombre.
     *
     * @return grados ordenados alfabéticamente
     */
    List<Grado> findAllByOrderByNivelAscNombreGradoAsc();

    /**
     * Comprueba si existe un grado con el nombre y nivel indicados.
     *
     * @param nombreGrado nombre del grado
     * @param nivel nivel académico
     * @return {@code true} si el grado ya está registrado
     */
    boolean existsByNombreGradoAndNivel(String nombreGrado, String nivel);
}
