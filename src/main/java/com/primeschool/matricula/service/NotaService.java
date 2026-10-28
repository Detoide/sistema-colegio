package com.primeschool.matricula.service;

import com.primeschool.matricula.dto.NotaFilaDTO;
import com.primeschool.matricula.dto.NotaForm;
import com.primeschool.matricula.model.entity.Nota;
import com.primeschool.matricula.model.entity.Usuario;

import java.math.BigDecimal;
import java.util.List;

public interface NotaService {

    List<NotaFilaDTO> planilla(Integer seccionId, Integer periodoId, Integer cursoId, Usuario usuario);

    void guardar(NotaForm form, Usuario usuario);

    BigDecimal promedio(Integer matriculaId, Integer periodoId);

    List<Nota> notasPorMatriculaYPeriodo(Integer matriculaId, Integer periodoId);

    List<Nota> notasPorMatricula(Integer matriculaId);
}
