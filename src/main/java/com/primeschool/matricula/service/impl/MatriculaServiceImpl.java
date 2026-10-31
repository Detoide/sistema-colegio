package com.primeschool.matricula.service.impl;

import com.primeschool.matricula.dto.MatriculaRequest;
import com.primeschool.matricula.dto.VacanteDTO;
import com.primeschool.matricula.exception.RecursoNoEncontradoException;
import com.primeschool.matricula.exception.ReglaNegocioException;
import com.primeschool.matricula.model.entity.Estudiante;
import com.primeschool.matricula.model.entity.Matricula;
import com.primeschool.matricula.model.entity.PeriodoAcademico;
import com.primeschool.matricula.model.entity.Seccion;
import com.primeschool.matricula.model.entity.Usuario;
import com.primeschool.matricula.model.enums.EstadoMatricula;
import com.primeschool.matricula.repository.EstudianteRepository;
import com.primeschool.matricula.repository.MatriculaRepository;
import com.primeschool.matricula.repository.PeriodoAcademicoRepository;
import com.primeschool.matricula.repository.SeccionRepository;
import com.primeschool.matricula.service.MatriculaService;
import com.primeschool.matricula.service.PeriodoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de matrículas.
 *
 * <p>Controla la disponibilidad de vacantes y aplica las reglas de negocio
 * para registrar, confirmar y anular matrículas.</p>
 */
@Service
public class MatriculaServiceImpl implements MatriculaService {

    private static final List<EstadoMatricula> ESTADOS_ACTIVOS =
            List.of(EstadoMatricula.PENDIENTE, EstadoMatricula.CONFIRMADA);

    private final MatriculaRepository matriculaRepository;
    private final EstudianteRepository estudianteRepository;
    private final SeccionRepository seccionRepository;
    private final PeriodoAcademicoRepository periodoRepository;
    private final PeriodoService periodoService;

    /**
     * Crea el servicio con sus repositorios y el servicio de periodos.
     *
     * @param matriculaRepository repositorio de matrículas
     * @param estudianteRepository repositorio de estudiantes
     * @param seccionRepository repositorio de secciones
     * @param periodoRepository repositorio de periodos
     * @param periodoService servicio de periodos
     */
    public MatriculaServiceImpl(MatriculaRepository matriculaRepository,
                                EstudianteRepository estudianteRepository,
                                SeccionRepository seccionRepository,
                                PeriodoAcademicoRepository periodoRepository,
                                PeriodoService periodoService) {
        this.matriculaRepository = matriculaRepository;
        this.estudianteRepository = estudianteRepository;
        this.seccionRepository = seccionRepository;
        this.periodoRepository = periodoRepository;
        this.periodoService = periodoService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Matricula> listar() {
        return matriculaRepository.findAllByOrderByPeriodo_FechaInicioDescEstudiante_ApellidosAsc();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Matricula> listarPorPeriodo(Integer idPeriodo) {
        return matriculaRepository.findByPeriodo_IdPeriodoOrderByEstudiante_ApellidosAsc(idPeriodo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Matricula> listarPorSeccionYPeriodo(Integer idSeccion, Integer idPeriodo) {
        return matriculaRepository.findBySeccion_IdSeccionAndPeriodo_IdPeriodoOrderByEstudiante_ApellidosAsc(
                idSeccion, idPeriodo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Matricula> listarVigentesPorSeccionYPeriodo(Integer idSeccion, Integer idPeriodo) {
        return matriculaRepository.findBySeccion_IdSeccionAndPeriodo_IdPeriodoAndEstadoInOrderByEstudiante_ApellidosAsc(
                idSeccion, idPeriodo, ESTADOS_ACTIVOS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Matricula> listarVisibles(Usuario usuario) {
        String rol = usuario.getRol().getNombreRol().toUpperCase();
        return switch (rol) {
            case "ADMINISTRADOR", "DIRECTIVO" -> listar();
            case "DOCENTE" -> matriculaRepository
                    .findBySeccion_Tutor_IdUsuarioOrderByPeriodo_FechaInicioDescEstudiante_ApellidosAsc(
                            usuario.getIdUsuario());
            case "APODERADO" -> matriculaRepository
                    .findByEstudiante_Apoderado_Usuario_IdUsuarioOrderByPeriodo_FechaInicioDescEstudiante_ApellidosAsc(
                            usuario.getIdUsuario());
            default -> List.of();
        };
    }

    /**
     * {@inheritDoc}
     *
     * @throws RecursoNoEncontradoException si la matrícula no existe
     */
    @Override
    public Matricula obtener(Integer id) {
        return matriculaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada"));
    }

    /**
     * {@inheritDoc}
     *
     * @throws RecursoNoEncontradoException si el estudiante, periodo o sección no existen
     * @throws ReglaNegocioException si el periodo, la matrícula o las vacantes no son válidos
     */
    @Override
    @Transactional
    public Matricula registrar(MatriculaRequest request) {
        Estudiante estudiante = estudianteRepository.findById(request.getEstudianteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado"));
        PeriodoAcademico periodo = periodoRepository.findById(request.getPeriodoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Periodo no encontrado"));
        if (!periodoService.esAnual(periodo)) {
            throw new ReglaNegocioException("La matrícula debe registrarse en un periodo anual");
        }

        Seccion seccion = seccionRepository.findByIdForUpdate(request.getSeccionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Sección no encontrada"));

        Optional<Matricula> existente = matriculaRepository
                .findByEstudiante_IdEstudianteAndPeriodo_IdPeriodo(
                        estudiante.getIdEstudiante(), periodo.getIdPeriodo());

        if (existente.isPresent() && ESTADOS_ACTIVOS.contains(existente.get().getEstado())) {
            throw new ReglaNegocioException("El estudiante ya tiene una matrícula vigente en ese periodo");
        }

        long activas = matriculaRepository.countBySeccion_IdSeccionAndEstadoIn(
                seccion.getIdSeccion(), ESTADOS_ACTIVOS);
        if (activas >= seccion.getVacantesTotales()) {
            throw new ReglaNegocioException("La sección no tiene vacantes disponibles");
        }

        Matricula matricula = existente.orElseGet(Matricula::new);
        matricula.setEstudiante(estudiante);
        matricula.setSeccion(seccion);
        matricula.setPeriodo(periodo);
        matricula.setFechaMatricula(LocalDate.now());
        matricula.setEstado(EstadoMatricula.PENDIENTE);
        matriculaRepository.save(matricula);

        seccion.setVacantesDisponibles((int) Math.max(0, seccion.getVacantesTotales() - activas - 1));
        seccionRepository.save(seccion);
        return matricula;
    }

    /**
     * {@inheritDoc}
     *
     * @throws RecursoNoEncontradoException si la matrícula o la sección no existen
     */
    @Override
    @Transactional
    public void anular(Integer id) {
        Matricula matricula = obtener(id);
        if (matricula.getEstado() == EstadoMatricula.ANULADA) {
            return;
        }
        matricula.setEstado(EstadoMatricula.ANULADA);
        matriculaRepository.save(matricula);

        Seccion seccion = seccionRepository.findByIdForUpdate(matricula.getSeccion().getIdSeccion())
                .orElseThrow(() -> new RecursoNoEncontradoException("Sección no encontrada"));
        long activas = matriculaRepository.countBySeccion_IdSeccionAndEstadoIn(
                seccion.getIdSeccion(), ESTADOS_ACTIVOS);
        seccion.setVacantesDisponibles((int) Math.max(0, seccion.getVacantesTotales() - activas));
        seccionRepository.save(seccion);
    }

    /**
     * {@inheritDoc}
     *
     * @throws RecursoNoEncontradoException si la matrícula no existe
     * @throws ReglaNegocioException si la matrícula no está pendiente
     */
    @Override
    @Transactional
    public void confirmar(Integer id) {
        Matricula matricula = obtener(id);
        if (matricula.getEstado() != EstadoMatricula.PENDIENTE) {
            throw new ReglaNegocioException("Solo se puede confirmar una matrícula pendiente");
        }
        matricula.setEstado(EstadoMatricula.CONFIRMADA);
        matriculaRepository.save(matricula);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<VacanteDTO> consultarVacantes() {
        return seccionRepository.findAllByOrderByGrado_NombreGradoAscNombreSeccionAsc().stream()
                .map(s -> new VacanteDTO(
                        s.getIdSeccion(),
                        s.getGrado().getNombreGrado(),
                        s.getNombreSeccion(),
                        s.getTutor() != null ? s.getTutor().getNombreCompleto() : "Sin tutor",
                        s.getVacantesTotales(),
                        s.getVacantesDisponibles()))
                .toList();
    }
}
