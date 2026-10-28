package com.primeschool.matricula.service;

import com.primeschool.matricula.dto.AsistenciaFilaDTO;
import com.primeschool.matricula.dto.AsistenciaForm;
import com.primeschool.matricula.model.entity.Usuario;
import com.primeschool.matricula.model.enums.EstadoAsistencia;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AsistenciaService {

    List<AsistenciaFilaDTO> planilla(Integer seccionId, LocalDate fecha, Usuario usuario);

    void guardar(AsistenciaForm form, Usuario usuario);

    Map<EstadoAsistencia, Long> resumen(Integer matriculaId, LocalDate desde, LocalDate hasta);
}
