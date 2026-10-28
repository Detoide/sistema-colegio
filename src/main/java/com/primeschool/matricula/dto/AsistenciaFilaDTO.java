package com.primeschool.matricula.dto;

import com.primeschool.matricula.model.enums.EstadoAsistencia;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AsistenciaFilaDTO {

    private Integer matriculaId;
    private String codigo;
    private String estudiante;
    private EstadoAsistencia estadoActual;
}
