package com.primeschool.matricula.service;

import com.primeschool.matricula.dto.BoletaDTO;
import com.primeschool.matricula.model.entity.Usuario;

public interface BoletaService {

    BoletaDTO generar(Integer idMatricula, Integer idPeriodo, Usuario usuario);
}
