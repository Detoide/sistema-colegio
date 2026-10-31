package com.primeschool.matricula.service;

import com.primeschool.matricula.dto.EstudianteRequest;
import com.primeschool.matricula.model.entity.Estudiante;

import java.util.List;

/**
 * Servicio de gestión de estudiantes.
 */
public interface EstudianteService {

    /**
     * Lista todos los estudiantes registrados.
     *
     * @return estudiantes registrados
     */
    List<Estudiante> listar();

    /**
     * Busca estudiantes por código, nombres o apellidos.
     *
     * @param q texto de búsqueda
     * @return estudiantes que coinciden con el texto
     */
    List<Estudiante> buscar(String q);

    /**
     * Obtiene un estudiante por su identificador.
     *
     * @param id identificador del estudiante
     * @return estudiante encontrado
     */
    Estudiante obtener(Integer id);

    /**
     * Registra o actualiza un estudiante.
     *
     * @param request datos del estudiante
     * @return estudiante guardado
     */
    Estudiante guardar(EstudianteRequest request);

    /**
     * Lista los estudiantes a cargo de un apoderado.
     *
     * @param idUsuario identificador del usuario apoderado
     * @return estudiantes del apoderado
     */
    List<Estudiante> listarPorApoderado(Integer idUsuario);
}
