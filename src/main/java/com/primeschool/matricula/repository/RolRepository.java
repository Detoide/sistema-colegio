package com.primeschool.matricula.repository;

import com.primeschool.matricula.model.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio de acceso a los roles del sistema.
 */
public interface RolRepository extends JpaRepository<Rol, Integer> {

    /**
     * Busca un rol por su nombre.
     *
     * @param nombreRol nombre del rol
     * @return rol encontrado, si existe
     */
    Optional<Rol> findByNombreRol(String nombreRol);
}
