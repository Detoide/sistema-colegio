package com.primeschool.matricula.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Formulario para registrar notas de una sección en un curso y periodo.
 */
@Getter
@Setter
@NoArgsConstructor
public class NotaForm {

    private Integer seccionId;
    private Integer periodoId;
    private Integer cursoId;
    private Map<Integer, BigDecimal> notas = new LinkedHashMap<>();
}
