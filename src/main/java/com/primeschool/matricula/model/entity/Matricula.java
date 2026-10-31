package com.primeschool.matricula.model.entity;

import com.primeschool.matricula.model.enums.EstadoMatricula;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Matrícula de un estudiante en una sección y periodo académico.
 */
@Entity
@Table(name = "matricula")
@Getter
@Setter
@NoArgsConstructor
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_matricula")
    private Integer idMatricula;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_estudiante", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_seccion", nullable = false)
    private Seccion seccion;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_periodo", nullable = false)
    private PeriodoAcademico periodo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_matricula", nullable = false)
    private LocalDate fechaMatricula;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoMatricula estado;

    /**
     * Devuelve el nombre completo del estudiante matriculado.
     *
     * @return nombre del estudiante o cadena vacía si no está asociado
     */
    public String getEstudianteNombre() {
        return estudiante != null ? estudiante.getNombreCompleto() : "";
    }

    /**
     * Devuelve la fecha de matrícula formateada para su presentación.
     *
     * @return fecha en formato dd/MM/yyyy o cadena vacía si no está definida
     */
    public String getFechaMatriculaTexto() {
        return fechaMatricula != null
                ? fechaMatricula.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "";
    }
}
