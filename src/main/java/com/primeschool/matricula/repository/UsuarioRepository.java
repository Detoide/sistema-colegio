package com.primeschool.matricula.repository;

import com.primeschool.matricula.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de acceso a los usuarios del sistema.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param correo correo del usuario
     * @return usuario encontrado, si existe
     */
    Optional<Usuario> findByCorreo(String correo);

    /**
     * Comprueba si existe un usuario con el correo indicado.
     *
     * @param correo correo a verificar
     * @return {@code true} si el correo ya está registrado
     */
    boolean existsByCorreo(String correo);

    /**
     * Lista los usuarios de un rol ordenados por apellidos.
     *
     * @param nombreRol nombre del rol
     * @return usuarios del rol indicado
     */
    List<Usuario> findByRol_NombreRolOrderByApellidosAsc(String nombreRol);

    /**
     * Lista todos los usuarios ordenados por apellidos y nombres.
     *
     * @return usuarios ordenados alfabéticamente
     */
    List<Usuario> findAllByOrderByApellidosAscNombresAsc();
}
