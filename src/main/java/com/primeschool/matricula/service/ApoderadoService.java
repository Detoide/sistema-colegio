package com.primeschool.matricula.service;

import com.primeschool.matricula.dto.ApoderadoRequest;
import com.primeschool.matricula.model.entity.Apoderado;

import java.util.List;

/**
 * Servicio de gestión de apoderados.
 */
public interface ApoderadoService {

    /**
     * Lista todos los apoderados registrados.
     *
     * @return apoderados registrados
     */
    List<Apoderado> listar();

    /**
     * Crea un apoderado y su usuario asociado.
     *
     * @param request datos del apoderado
     * @return apoderado creado
     */
    Apoderado crear(ApoderadoRequest request);

    /**
     * Obtiene el apoderado asociado a un usuario.
     *
     * @param idUsuario identificador del usuario
     * @return apoderado encontrado
     */
    Apoderado obtenerPorUsuario(Integer idUsuario);
}
