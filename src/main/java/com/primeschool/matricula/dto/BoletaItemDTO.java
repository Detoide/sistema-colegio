package com.primeschool.matricula.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class BoletaItemDTO {

    private String curso;
    private BigDecimal calificacion;
}
