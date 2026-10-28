package com.primeschool.matricula.service;

import com.primeschool.matricula.dto.MatriculaRequest;
import com.primeschool.matricula.dto.VacanteDTO;
import com.primeschool.matricula.model.entity.Matricula;
import com.primeschool.matricula.model.entity.Usuario;

import java.util.List;

public interface MatriculaService {

    List<Matricula> listar();

    List<Matricula> listarPorPeriodo(Integer idPeriodo);

    List<Matricula> listarPorSeccionYPeriodo(Integer idSeccion, Integer idPeriodo);

    List<Matricula> listarVigentesPorSeccionYPeriodo(Integer idSeccion, Integer idPeriodo);

    List<Matricula> listarVisibles(Usuario usuario);

    Matricula obtener(Integer id);

    Matricula registrar(MatriculaRequest request);

    void anular(Integer id);

    void confirmar(Integer id);

    List<VacanteDTO> consultarVacantes();
}
