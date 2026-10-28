package com.primeschool.matricula.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class RegistroAcademicoForm {

    private Integer seccionId;
    private Integer periodoId;
    private Integer cursoId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;

    private Map<Integer, BigDecimal> notas = new LinkedHashMap<>();
    private Map<Integer, String> estados = new LinkedHashMap<>();
}
