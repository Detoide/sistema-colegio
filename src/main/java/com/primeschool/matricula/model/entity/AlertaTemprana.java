package com.primeschool.matricula.model.entity;

import com.primeschool.matricula.model.enums.TipoRiesgo;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Alerta generada para advertir un riesgo académico o de asistencia.
 */
@Entity
@Table(name = "alerta_temprana")
@Getter
@Setter
@NoArgsConstructor
public class AlertaTemprana {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerta")
    private Integer idAlerta;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_matricula", nullable = false)
    private Matricula matricula;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_riesgo", nullable = false, length = 30)
    private TipoRiesgo tipoRiesgo;

    @Column(name = "descripcion", nullable = false, length = 255)
    private String descripcion;

    @Column(name = "fecha_generada", nullable = false)
    private LocalDateTime fechaGenerada;

    @Column(name = "atendida", nullable = false)
    private Boolean atendida = false;

    @PrePersist
    void prePersist() {
        if (fechaGenerada == null) {
            fechaGenerada = LocalDateTime.now();
        }
        if (atendida == null) {
            atendida = false;
        }
    }

    /**
     * Devuelve el nombre del estudiante asociado a la alerta.
     *
     * @return nombre del estudiante o cadena vacía si no está asociado
     */
    public String getEstudianteNombre() {
        return matricula != null ? matricula.getEstudianteNombre() : "";
    }

    /**
     * Devuelve la fecha de generación formateada para su presentación.
     *
     * @return fecha en formato dd/MM/yyyy HH:mm o cadena vacía si no está definida
     */
    public String getFechaGeneradaTexto() {
        return fechaGenerada != null
                ? fechaGenerada.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "";
    }
}
