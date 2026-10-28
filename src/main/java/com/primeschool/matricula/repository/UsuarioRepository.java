package com.primeschool.matricula.repository;

import com.primeschool.matricula.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    List<Usuario> findByRol_NombreRolOrderByApellidosAsc(String nombreRol);

    List<Usuario> findAllByOrderByApellidosAscNombresAsc();
}
