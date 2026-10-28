package com.primeschool.matricula.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "apoderado")
@Getter
@Setter
@NoArgsConstructor
public class Apoderado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_apoderado")
    private Integer idApoderado;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "dni", nullable = false, unique = true, length = 8)
    private String dni;

    @Column(name = "telefono", length = 15)
    private String telefono;

    public String getNombreCompleto() {
        return usuario != null ? usuario.getNombreCompleto() : "";
    }

    public String getCorreo() {
        return usuario != null ? usuario.getCorreo() : "";
    }
}
