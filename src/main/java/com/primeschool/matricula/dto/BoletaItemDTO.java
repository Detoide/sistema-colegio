package com.primeschool.matricula.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Calificación de un curso dentro de una boleta.
 */
@Getter
@Setter
@AllArgsConstructor
public class BoletaItemDTO {

    private String curso;
    private BigDecimal calificacion;
}
