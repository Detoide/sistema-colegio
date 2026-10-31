package com.primeschool.matricula.dto;

import com.primeschool.matricula.model.entity.AlertaTemprana;
import com.primeschool.matricula.model.entity.Estudiante;
import com.primeschool.matricula.model.entity.Seccion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Datos resumidos que se muestran en el panel principal según el rol.
 */
@Getter
@Setter
@NoArgsConstructor
public class DashboardDTO {

    private String nombreUsuario;
    private String rol;
    private long estudiantes;
    private long secciones;
    private long matriculas;
    private long alertasActivas;
    private List<Seccion> seccionesDocente = new ArrayList<>();
    private List<Estudiante> hijos = new ArrayList<>();
    private List<AlertaTemprana> alertas = new ArrayList<>();
}
