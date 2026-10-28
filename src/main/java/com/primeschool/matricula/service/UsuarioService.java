package com.primeschool.matricula.service;

import com.primeschool.matricula.dto.UsuarioRequest;
import com.primeschool.matricula.model.entity.Usuario;

import java.util.List;

public interface UsuarioService {

    Usuario obtenerPorCorreo(String correo);

    Usuario obtenerPorId(Integer id);

    List<Usuario> listar();

    List<Usuario> listarDocentes();

    Usuario crear(UsuarioRequest request);
}
