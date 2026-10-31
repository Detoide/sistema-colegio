package com.primeschool.matricula.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Formulario para registrar la asistencia de una sección en una fecha.
 */
@Getter
@Setter
@NoArgsConstructor
public class AsistenciaForm {

    private Integer seccionId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;

    private Map<Integer, String> estados = new LinkedHashMap<>();
}
