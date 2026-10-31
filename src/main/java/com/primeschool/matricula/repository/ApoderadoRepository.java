package com.primeschool.matricula.repository;

import com.primeschool.matricula.model.entity.Apoderado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de acceso a los apoderados registrados.
 */
public interface ApoderadoRepository extends JpaRepository<Apoderado, Integer> {

    /**
     * Busca el apoderado asociado a un usuario del sistema.
     *
     * @param idUsuario identificador del usuario
     * @return apoderado encontrado, si existe
     */
    Optional<Apoderado> findByUsuario_IdUsuario(Integer idUsuario);

    /**
     * Busca un apoderado por su DNI.
     *
     * @param dni documento nacional de identidad
     * @return apoderado encontrado, si existe
     */
    Optional<Apoderado> findByDni(String dni);

    /**
     * Comprueba si existe un apoderado con el DNI indicado.
     *
     * @param dni documento nacional de identidad
     * @return {@code true} si el DNI ya está registrado
     */
    boolean existsByDni(String dni);

    /**
     * Lista todos los apoderados ordenados por apellidos y nombres.
     *
     * @return apoderados ordenados alfabéticamente
     */
    List<Apoderado> findAllByOrderByUsuario_ApellidosAscUsuario_NombresAsc();
}
