package com.primeschool.matricula.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BoletaDTO {

    private Integer matriculaId;
    private String estudiante;
    private String codigo;
    private String grado;
    private String seccion;
    private String periodo;
    private List<BoletaItemDTO> notas = new ArrayList<>();
    private BigDecimal promedio;
    private long asistio;
    private long falta;
    private long tardanza;
    private long justificada;
}
