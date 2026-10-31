package com.primeschool.matricula.service;

import com.primeschool.matricula.dto.MatriculaRequest;
import com.primeschool.matricula.dto.VacanteDTO;
import com.primeschool.matricula.model.entity.Matricula;
import com.primeschool.matricula.model.entity.Usuario;

import java.util.List;

/**
 * Servicio de gestión de matrículas y vacantes.
 */
public interface MatriculaService {

    /**
     * Lista todas las matrículas.
     *
     * @return matrículas registradas
     */
    List<Matricula> listar();

    /**
     * Lista las matrículas de un periodo.
     *
     * @param idPeriodo identificador del periodo
     * @return matrículas del periodo
     */
    List<Matricula> listarPorPeriodo(Integer idPeriodo);

    /**
     * Lista las matrículas de una sección en un periodo.
     *
     * @param idSeccion identificador de la sección
     * @param idPeriodo identificador del periodo
     * @return matrículas de la sección
     */
    List<Matricula> listarPorSeccionYPeriodo(Integer idSeccion, Integer idPeriodo);

    /**
     * Lista las matrículas vigentes de una sección en un periodo.
     *
     * @param idSeccion identificador de la sección
     * @param idPeriodo identificador del periodo
     * @return matrículas vigentes
     */
    List<Matricula> listarVigentesPorSeccionYPeriodo(Integer idSeccion, Integer idPeriodo);

    /**
     * Lista las matrículas visibles para el usuario según su rol.
     *
     * @param usuario usuario que consulta las matrículas
     * @return matrículas visibles
     */
    List<Matricula> listarVisibles(Usuario usuario);

    /**
     * Obtiene una matrícula por su identificador.
     *
     * @param id identificador de la matrícula
     * @return matrícula encontrada
     */
    Matricula obtener(Integer id);

    /**
     * Registra una nueva matrícula y reserva la vacante correspondiente.
     *
     * @param request datos de la matrícula
     * @return matrícula registrada
     */
    Matricula registrar(MatriculaRequest request);

    /**
     * Anula una matrícula y libera su vacante.
     *
     * @param id identificador de la matrícula
     */
    void anular(Integer id);

    /**
     * Confirma una matrícula pendiente.
     *
     * @param id identificador de la matrícula
     */
    void confirmar(Integer id);

    /**
     * Consulta las vacantes disponibles por sección.
     *
     * @return resumen de vacantes
     */
    List<VacanteDTO> consultarVacantes();
}
