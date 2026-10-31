package com.primeschool.matricula.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Datos de entrada para registrar o actualizar un estudiante.
 */
@Getter
@Setter
@NoArgsConstructor
public class EstudianteRequest {

    private Integer idEstudiante;

    @NotNull(message = "Debe seleccionar un apoderado")
    private Integer apoderadoId;

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 15, message = "El código no puede superar los 15 caracteres")
    private String codigo;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100, message = "Los nombres no pueden superar los 100 caracteres")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Los apellidos no pueden superar los 100 caracteres")
    private String apellidos;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaNacimiento;
}
