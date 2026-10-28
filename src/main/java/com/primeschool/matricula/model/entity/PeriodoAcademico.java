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
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "periodo_academico")
@Getter
@Setter
@NoArgsConstructor
public class PeriodoAcademico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_periodo")
    private Integer idPeriodo;

    @Column(name = "nombre_periodo", nullable = false, unique = true, length = 30)
    private String nombrePeriodo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    public boolean contiene(LocalDate fecha) {
        return fecha != null
                && !fecha.isBefore(fechaInicio)
                && !fecha.isAfter(fechaFin);
    }

    public boolean contiene(PeriodoAcademico otro) {
        return otro != null
                && !otro.getFechaInicio().isBefore(fechaInicio)
                && !otro.getFechaFin().isAfter(fechaFin);
    }
}
