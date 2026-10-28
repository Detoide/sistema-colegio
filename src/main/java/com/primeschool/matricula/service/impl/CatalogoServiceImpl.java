package com.primeschool.matricula.service.impl;

import com.primeschool.matricula.dto.VacanteDTO;
import com.primeschool.matricula.exception.RecursoNoEncontradoException;
import com.primeschool.matricula.exception.ReglaNegocioException;
import com.primeschool.matricula.model.entity.Curso;
import com.primeschool.matricula.model.entity.Grado;
import com.primeschool.matricula.model.entity.Seccion;
import com.primeschool.matricula.model.entity.Usuario;
import com.primeschool.matricula.repository.CursoRepository;
import com.primeschool.matricula.repository.GradoRepository;
import com.primeschool.matricula.repository.SeccionRepository;
import com.primeschool.matricula.repository.UsuarioRepository;
import com.primeschool.matricula.service.CatalogoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CatalogoServiceImpl implements CatalogoService {

    private final GradoRepository gradoRepository;
    private final CursoRepository cursoRepository;
    private final SeccionRepository seccionRepository;
    private final UsuarioRepository usuarioRepository;

    public CatalogoServiceImpl(GradoRepository gradoRepository,
                               CursoRepository cursoRepository,
                               SeccionRepository seccionRepository,
                               UsuarioRepository usuarioRepository) {
        this.gradoRepository = gradoRepository;
        this.cursoRepository = cursoRepository;
        this.seccionRepository = seccionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<Grado> listarGrados() {
        return gradoRepository.findAllByOrderByNivelAscNombreGradoAsc();
    }

    @Override
    public List<Curso> listarCursos() {
        return cursoRepository.findAllByOrderByNombreCursoAsc();
    }

    @Override
    public List<Seccion> listarSecciones() {
        return seccionRepository.findAllByOrderByGrado_NombreGradoAscNombreSeccionAsc();
    }

    @Override
    public List<Seccion> listarSeccionesPorTutor(Integer idUsuario) {
        return seccionRepository.findByTutor_IdUsuarioOrderByGrado_NombreGradoAscNombreSeccionAsc(idUsuario);
    }

    @Override
    public List<VacanteDTO> listarVacantes() {
        return listarSecciones().stream()
                .map(s -> new VacanteDTO(
                        s.getIdSeccion(),
                        s.getGrado().getNombreGrado(),
                        s.getNombreSeccion(),
                        s.getTutor() != null ? s.getTutor().getNombreCompleto() : "Sin tutor",
                        s.getVacantesTotales(),
                        s.getVacantesDisponibles()))
                .toList();
    }

    @Override
    @Transactional
    public void crearGrado(String nombreGrado, String nivel) {
        if (nombreGrado == null || nombreGrado.isBlank() || nivel == null || nivel.isBlank()) {
            throw new ReglaNegocioException("Nombre y nivel del grado son obligatorios");
        }
        if (gradoRepository.existsByNombreGradoAndNivel(nombreGrado.trim(), nivel.trim())) {
            throw new ReglaNegocioException("Ya existe ese grado y nivel");
        }
        Grado grado = new Grado();
        grado.setNombreGrado(nombreGrado.trim());
        grado.setNivel(nivel.trim());
        gradoRepository.save(grado);
    }

    @Override
    @Transactional
    public void crearCurso(String nombreCurso, String competencia) {
        if (nombreCurso == null || nombreCurso.isBlank()) {
            throw new ReglaNegocioException("El nombre del curso es obligatorio");
        }
        if (cursoRepository.existsByNombreCurso(nombreCurso.trim())) {
            throw new ReglaNegocioException("Ya existe un curso con ese nombre");
        }
        Curso curso = new Curso();
        curso.setNombreCurso(nombreCurso.trim());
        curso.setCompetencia(competencia);
        cursoRepository.save(curso);
    }

    @Override
    @Transactional
    public void crearSeccion(Integer idGrado, Integer idTutor, String nombreSeccion, Integer vacantesTotales) {
        if (idGrado == null || idTutor == null || nombreSeccion == null || nombreSeccion.isBlank()) {
            throw new ReglaNegocioException("Grado, tutor y nombre de sección son obligatorios");
        }
        if (vacantesTotales == null || vacantesTotales <= 0) {
            throw new ReglaNegocioException("Las vacantes deben ser mayores a cero");
        }
        Grado grado = gradoRepository.findById(idGrado)
                .orElseThrow(() -> new RecursoNoEncontradoException("Grado no encontrado"));
        Usuario tutor = usuarioRepository.findById(idTutor)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tutor no encontrado"));
        if (!"DOCENTE".equalsIgnoreCase(tutor.getRol().getNombreRol())) {
            throw new ReglaNegocioException("El tutor debe tener rol DOCENTE");
        }
        if (seccionRepository.existsByGrado_IdGradoAndNombreSeccion(idGrado, nombreSeccion.trim())) {
            throw new ReglaNegocioException("Ya existe una sección con ese nombre en el grado");
        }

        Seccion seccion = new Seccion();
        seccion.setGrado(grado);
        seccion.setTutor(tutor);
        seccion.setNombreSeccion(nombreSeccion.trim());
        seccion.setVacantesTotales(vacantesTotales);
        seccion.setVacantesDisponibles(vacantesTotales);
        seccionRepository.save(seccion);
    }
}
