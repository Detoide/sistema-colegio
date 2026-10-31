package com.primeschool.matricula.service.impl;

import com.primeschool.matricula.dto.EstudianteRequest;
import com.primeschool.matricula.exception.RecursoNoEncontradoException;
import com.primeschool.matricula.exception.ReglaNegocioException;
import com.primeschool.matricula.model.entity.Apoderado;
import com.primeschool.matricula.model.entity.Estudiante;
import com.primeschool.matricula.repository.ApoderadoRepository;
import com.primeschool.matricula.repository.EstudianteRepository;
import com.primeschool.matricula.service.EstudianteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de estudiantes.
 */
@Service
public class EstudianteServiceImpl implements EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final ApoderadoRepository apoderadoRepository;

    /**
     * Crea el servicio con los repositorios de estudiantes y apoderados.
     *
     * @param estudianteRepository repositorio de estudiantes
     * @param apoderadoRepository repositorio de apoderados
     */
    public EstudianteServiceImpl(EstudianteRepository estudianteRepository,
                                 ApoderadoRepository apoderadoRepository) {
        this.estudianteRepository = estudianteRepository;
        this.apoderadoRepository = apoderadoRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Estudiante> listar() {
        return estudianteRepository.findAllByOrderByApellidosAscNombresAsc();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Estudiante> buscar(String q) {
        if (q == null || q.isBlank()) {
            return listar();
        }
        return estudianteRepository.buscar(q.trim());
    }

    /**
     * {@inheritDoc}
     *
     * @throws RecursoNoEncontradoException si el estudiante no existe
     */
    @Override
    public Estudiante obtener(Integer id) {
        return estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado"));
    }

    /**
     * {@inheritDoc}
     *
     * @throws ReglaNegocioException si el apoderado no existe o el código está duplicado
     */
    @Override
    @Transactional
    public Estudiante guardar(EstudianteRequest request) {
        Apoderado apoderado = apoderadoRepository.findById(request.getApoderadoId())
                .orElseThrow(() -> new ReglaNegocioException("Apoderado no encontrado"));

        Estudiante estudiante;
        if (request.getIdEstudiante() != null) {
            estudiante = obtener(request.getIdEstudiante());
            if (estudianteRepository.existsByCodigoAndIdEstudianteNot(
                    request.getCodigo().trim(), request.getIdEstudiante())) {
                throw new ReglaNegocioException("Ya existe un estudiante con ese código");
            }
        } else {
            if (estudianteRepository.existsByCodigo(request.getCodigo().trim())) {
                throw new ReglaNegocioException("Ya existe un estudiante con ese código");
            }
            estudiante = new Estudiante();
        }

        estudiante.setApoderado(apoderado);
        estudiante.setCodigo(request.getCodigo().trim());
        estudiante.setNombres(request.getNombres().trim());
        estudiante.setApellidos(request.getApellidos().trim());
        estudiante.setFechaNacimiento(request.getFechaNacimiento());
        return estudianteRepository.save(estudiante);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Estudiante> listarPorApoderado(Integer idUsuario) {
        return estudianteRepository.findByApoderado_Usuario_IdUsuarioOrderByApellidosAscNombresAsc(idUsuario);
    }
}
