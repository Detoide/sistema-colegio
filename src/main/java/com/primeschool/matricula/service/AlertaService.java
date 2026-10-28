package com.primeschool.matricula.service;

import com.primeschool.matricula.model.entity.AlertaTemprana;
import com.primeschool.matricula.model.entity.Usuario;

import java.util.List;

public interface AlertaService {

    List<AlertaTemprana> listarVisibles(Usuario usuario);

    void atender(Integer idAlerta, Usuario usuario);

    void evaluarRiesgoAcademico(Integer idMatricula, Integer idPeriodo);

    void evaluarInasistencia(Integer idMatricula);

    long contarActivas();
}
