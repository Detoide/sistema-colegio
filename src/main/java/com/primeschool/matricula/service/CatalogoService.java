package com.primeschool.matricula.service;

import com.primeschool.matricula.dto.VacanteDTO;
import com.primeschool.matricula.model.entity.Curso;
import com.primeschool.matricula.model.entity.Grado;
import com.primeschool.matricula.model.entity.Seccion;

import java.util.List;

public interface CatalogoService {

    List<Grado> listarGrados();

    List<Curso> listarCursos();

    List<Seccion> listarSecciones();

    List<Seccion> listarSeccionesPorTutor(Integer idUsuario);

    List<VacanteDTO> listarVacantes();

    void crearGrado(String nombreGrado, String nivel);

    void crearCurso(String nombreCurso, String competencia);

    void crearSeccion(Integer idGrado, Integer idTutor, String nombreSeccion, Integer vacantesTotales);
}
