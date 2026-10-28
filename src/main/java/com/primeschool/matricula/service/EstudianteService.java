package com.primeschool.matricula.service;

import com.primeschool.matricula.dto.EstudianteRequest;
import com.primeschool.matricula.model.entity.Estudiante;

import java.util.List;

public interface EstudianteService {

    List<Estudiante> listar();

    List<Estudiante> buscar(String q);

    Estudiante obtener(Integer id);

    Estudiante guardar(EstudianteRequest request);

    List<Estudiante> listarPorApoderado(Integer idUsuario);
}
