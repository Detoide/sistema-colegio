package com.primeschool.matricula.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class NotaFilaDTO {

    private Integer matriculaId;
    private String codigo;
    private String estudiante;
    private BigDecimal notaActual;
}
