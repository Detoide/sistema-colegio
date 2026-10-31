package com.primeschool.matricula.service;

import com.primeschool.matricula.dto.VacanteDTO;
import com.primeschool.matricula.model.entity.Curso;
import com.primeschool.matricula.model.entity.Grado;
import com.primeschool.matricula.model.entity.Seccion;

import java.util.List;

/**
 * Servicio de consulta y administración de catálogos académicos.
 */
public interface CatalogoService {

    /**
     * Lista los grados disponibles.
     *
     * @return grados registrados
     */
    List<Grado> listarGrados();

    /**
     * Lista los cursos disponibles.
     *
     * @return cursos registrados
     */
    List<Curso> listarCursos();

    /**
     * Lista todas las secciones disponibles.
     *
     * @return secciones registradas
     */
    List<Seccion> listarSecciones();

    /**
     * Lista las secciones asignadas a un docente tutor.
     *
     * @param idUsuario identificador del usuario tutor
     * @return secciones del tutor
     */
    List<Seccion> listarSeccionesPorTutor(Integer idUsuario);

    /**
     * Lista el resumen de vacantes por sección.
     *
     * @return vacantes disponibles y totales
     */
    List<VacanteDTO> listarVacantes();

    /**
     * Crea un grado académico.
     *
     * @param nombreGrado nombre del grado
     * @param nivel nivel académico
     */
    void crearGrado(String nombreGrado, String nivel);

    /**
     * Crea un curso.
     *
     * @param nombreCurso nombre del curso
     * @param competencia competencia asociada
     */
    void crearCurso(String nombreCurso, String competencia);

    /**
     * Crea una sección con su capacidad de vacantes.
     *
     * @param idGrado identificador del grado
     * @param idTutor identificador del docente tutor
     * @param nombreSeccion nombre de la sección
     * @param vacantesTotales cantidad total de vacantes
     */
    void crearSeccion(Integer idGrado, Integer idTutor, String nombreSeccion, Integer vacantesTotales);
}
