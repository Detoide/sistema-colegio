package com.primeschool.matricula.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Datos necesarios para registrar una matrícula.
 */
@Getter
@Setter
@NoArgsConstructor
public class MatriculaRequest {

    @NotNull(message = "Debe seleccionar un estudiante")
    private Integer estudianteId;

    @NotNull(message = "Debe seleccionar una sección")
    private Integer seccionId;

    @NotNull(message = "Debe seleccionar un periodo")
    private Integer periodoId;
}
