package com.primeschool.matricula.service;

import com.primeschool.matricula.dto.ApoderadoRequest;
import com.primeschool.matricula.model.entity.Apoderado;

import java.util.List;

public interface ApoderadoService {

    List<Apoderado> listar();

    Apoderado crear(ApoderadoRequest request);

    Apoderado obtenerPorUsuario(Integer idUsuario);
}
