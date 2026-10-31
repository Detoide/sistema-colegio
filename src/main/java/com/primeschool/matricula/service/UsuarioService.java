package com.primeschool.matricula.service;

import com.primeschool.matricula.dto.UsuarioRequest;
import com.primeschool.matricula.model.entity.Usuario;

import java.util.List;

/**
 * Servicio de gestión de usuarios del sistema.
 */
public interface UsuarioService {

    /**
     * Obtiene un usuario por su correo electrónico.
     *
     * @param correo correo del usuario
     * @return usuario encontrado
     */
    Usuario obtenerPorCorreo(String correo);

    /**
     * Obtiene un usuario por su identificador.
     *
     * @param id identificador del usuario
     * @return usuario encontrado
     */
    Usuario obtenerPorId(Integer id);

    /**
     * Lista todos los usuarios del sistema.
     *
     * @return usuarios registrados
     */
    List<Usuario> listar();

    /**
     * Lista los usuarios con rol de docente.
     *
     * @return docentes registrados
     */
    List<Usuario> listarDocentes();

    /**
     * Crea un nuevo usuario a partir de los datos recibidos.
     *
     * @param request datos del usuario
     * @return usuario creado
     */
    Usuario crear(UsuarioRequest request);
}
