package com.primeschool.matricula.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "estudiante")
@Getter
@Setter
@NoArgsConstructor
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estudiante")
    private Integer idEstudiante;

    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.EAGER, optional = false)
    @jakarta.persistence.JoinColumn(name = "id_apoderado", nullable = false)
    private Apoderado apoderado;

    @Column(name = "codigo", nullable = false, unique = true, length = 15)
    private String codigo;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    public String getNombreCompleto() {
        return apellidos + ", " + nombres;
    }

    public String getFechaNacimientoTexto() {
        return fechaNacimiento != null
                ? fechaNacimiento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "";
    }
}
