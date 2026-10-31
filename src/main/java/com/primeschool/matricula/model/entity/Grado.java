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

/**
 * Grado académico ofrecido por la institución.
 */
@Entity
@Table(name = "grado")
@Getter
@Setter
@NoArgsConstructor
public class Grado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_grado")
    private Integer idGrado;

    @Column(name = "nombre_grado", nullable = false, length = 30)
    private String nombreGrado;

    @Column(name = "nivel", nullable = false, length = 20)
    private String nivel;

    /**
     * Devuelve la descripción del grado con su nivel académico.
     *
     * @return descripción en formato nombre - nivel
     */
    public String getDescripcion() {
        return nombreGrado + " - " + nivel;
    }
}
