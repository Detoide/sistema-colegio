package com.primeschool.matricula.dto;

import com.primeschool.matricula.model.enums.EstadoAsistencia;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Fila con la nota y asistencia actuales de un estudiante.
 */
@Getter
@Setter
@AllArgsConstructor
public class RegistroAcademicoFilaDTO {

    private Integer matriculaId;
    private String codigo;
    private String estudiante;
    private BigDecimal notaActual;
    private EstadoAsistencia estadoActual;
}
