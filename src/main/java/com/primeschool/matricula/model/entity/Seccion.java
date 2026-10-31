package com.primeschool.matricula.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

/**
 * Sección de un grado, con su docente tutor y control de vacantes.
 */
@Entity
@Table(name = "seccion")
@Getter
@Setter
@NoArgsConstructor
public class Seccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_seccion")
    private Integer idSeccion;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_grado", nullable = false)
    private Grado grado;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_tutor", nullable = false)
    private Usuario tutor;

    @Column(name = "nombre_seccion", nullable = false, length = 5)
    private String nombreSeccion;

    @Column(name = "vacantes_totales", nullable = false)
    private Integer vacantesTotales;

    @Column(name = "vacantes_disponibles", nullable = false)
    private Integer vacantesDisponibles;

    /**
     * Devuelve la descripción de la sección con su grado.
     *
     * @return descripción en formato grado y nombre de sección
     */
    public String getDescripcion() {
        return grado.getNombreGrado() + " " + nombreSeccion;
    }

    /**
     * Devuelve el nombre del docente tutor de la sección.
     *
     * @return nombre del tutor o cadena vacía si no está asignado
     */
    public String getTutorNombre() {
        return tutor != null ? tutor.getNombreCompleto() : "";
    }
}
