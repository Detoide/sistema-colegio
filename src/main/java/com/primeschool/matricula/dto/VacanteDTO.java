package com.primeschool.matricula.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Resumen de vacantes disponibles por sección.
 */
@Getter
@Setter
@AllArgsConstructor
public class VacanteDTO {

    private Integer seccionId;
    private String grado;
    private String seccion;
    private String tutor;
    private Integer total;
    private Integer disponibles;
}
