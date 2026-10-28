package com.primeschool.matricula.service;

import com.primeschool.matricula.dto.MatriculaRequest;
import com.primeschool.matricula.exception.ReglaNegocioException;
import com.primeschool.matricula.model.entity.Apoderado;
import com.primeschool.matricula.model.entity.Estudiante;
import com.primeschool.matricula.model.entity.Grado;
import com.primeschool.matricula.model.entity.Matricula;
import com.primeschool.matricula.model.entity.PeriodoAcademico;
import com.primeschool.matricula.model.entity.Seccion;
import com.primeschool.matricula.model.entity.Usuario;
import com.primeschool.matricula.model.enums.EstadoMatricula;
import com.primeschool.matricula.repository.ApoderadoRepository;
import com.primeschool.matricula.repository.EstudianteRepository;
import com.primeschool.matricula.repository.GradoRepository;
import com.primeschool.matricula.repository.MatriculaRepository;
import com.primeschool.matricula.repository.PeriodoAcademicoRepository;
import com.primeschool.matricula.repository.SeccionRepository;
import com.primeschool.matricula.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MatriculaServiceIntegrationTest {

    @Autowired
    private MatriculaService matriculaService;
    @Autowired
    private PeriodoService periodoService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PeriodoAcademicoRepository periodoRepository;
    @Autowired
    private GradoRepository gradoRepository;
    @Autowired
    private SeccionRepository seccionRepository;
    @Autowired
    private EstudianteRepository estudianteRepository;
    @Autowired
    private ApoderadoRepository apoderadoRepository;
    @Autowired
    private MatriculaRepository matriculaRepository;

    private PeriodoAcademico anual;
    private PeriodoAcademico bimestre;
    private Seccion seccion;
    private Estudiante estudiante;
    private MatriculaRequest request;

    @BeforeEach
    void prepararDatos() {
        Usuario docente = usuarioRepository.findByCorreo("docente@colegio.test").orElseThrow();
        anual = periodoRepository.findAllByOrderByFechaInicioAsc().stream()
                .filter(periodoService::esAnual)
                .findFirst()
                .orElseThrow();
        bimestre = periodoRepository.findAllByOrderByFechaInicioAsc().stream()
                .filter(p -> !periodoService.esAnual(p))
                .findFirst()
                .orElseThrow();

        Grado grado = gradoRepository.findAllByOrderByNivelAscNombreGradoAsc().get(0);
        seccion = new Seccion();
        seccion.setGrado(grado);
        seccion.setTutor(docente);
        seccion.setNombreSeccion("TX");
        seccion.setVacantesTotales(2);
        seccion.setVacantesDisponibles(2);
        seccionRepository.save(seccion);

        Apoderado apoderado = apoderadoRepository.findAllByOrderByUsuario_ApellidosAscUsuario_NombresAsc()
                .get(0);
        estudiante = new Estudiante();
        estudiante.setApoderado(apoderado);
        estudiante.setCodigo("T" + (System.nanoTime() % 1_000_000_000L));
        estudiante.setNombres("Alumno");
        estudiante.setApellidos("Prueba");
        estudiante.setFechaNacimiento(LocalDate.of(2012, 5, 10));
        estudianteRepository.save(estudiante);

        request = new MatriculaRequest();
        request.setEstudianteId(estudiante.getIdEstudiante());
        request.setSeccionId(seccion.getIdSeccion());
        request.setPeriodoId(anual.getIdPeriodo());
    }

    @Test
    void registraMatriculaYDescuentaVacante() {
        Matricula matricula = matriculaService.registrar(request);

        assertThat(matricula.getEstado()).isEqualTo(EstadoMatricula.PENDIENTE);
        assertThat(matricula.getFechaMatricula()).isEqualTo(LocalDate.now());
        assertThat(seccionRepository.findById(seccion.getIdSeccion()).orElseThrow()
                .getVacantesDisponibles()).isEqualTo(1);
    }

    @Test
    void rechazaMatriculaDuplicadaVigente() {
        matriculaService.registrar(request);

        assertThatThrownBy(() -> matriculaService.registrar(request))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("ya tiene una matrícula vigente");
    }

    @Test
    void confirmaAnulaYReactivaLaMismaMatricula() {
        Matricula matricula = matriculaService.registrar(request);
        matriculaService.confirmar(matricula.getIdMatricula());

        assertThat(matriculaService.obtener(matricula.getIdMatricula()).getEstado())
                .isEqualTo(EstadoMatricula.CONFIRMADA);

        matriculaService.anular(matricula.getIdMatricula());

        assertThat(matriculaService.obtener(matricula.getIdMatricula()).getEstado())
                .isEqualTo(EstadoMatricula.ANULADA);
        assertThat(seccionRepository.findById(seccion.getIdSeccion()).orElseThrow()
                .getVacantesDisponibles()).isEqualTo(2);

        Matricula reactivada = matriculaService.registrar(request);

        assertThat(reactivada.getIdMatricula()).isEqualTo(matricula.getIdMatricula());
        assertThat(reactivada.getEstado()).isEqualTo(EstadoMatricula.PENDIENTE);
        assertThat(seccionRepository.findById(seccion.getIdSeccion()).orElseThrow()
                .getVacantesDisponibles()).isEqualTo(1);
    }

    @Test
    void rechazaMatriculaEnPeriodoNoAnual() {
        request.setPeriodoId(bimestre.getIdPeriodo());

        assertThatThrownBy(() -> matriculaService.registrar(request))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("periodo anual");
    }

    @Test
    void rechazaMatriculaSinVacantes() {
        matriculaService.registrar(request);
        seccion.setVacantesTotales(1);
        seccion.setVacantesDisponibles(0);

        Estudiante otro = new Estudiante();
        otro.setApoderado(estudiante.getApoderado());
        otro.setCodigo("T" + (System.nanoTime() % 1_000_000_000L));
        otro.setNombres("Otro");
        otro.setApellidos("SinVacante");
        otro.setFechaNacimiento(LocalDate.of(2012, 6, 11));
        estudianteRepository.save(otro);

        MatriculaRequest segundo = new MatriculaRequest();
        segundo.setEstudianteId(otro.getIdEstudiante());
        segundo.setSeccionId(seccion.getIdSeccion());
        segundo.setPeriodoId(anual.getIdPeriodo());

        assertThatThrownBy(() -> matriculaService.registrar(segundo))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("no tiene vacantes");
        assertThat(matriculaRepository.findByEstudiante_IdEstudianteAndPeriodo_IdPeriodo(
                otro.getIdEstudiante(), anual.getIdPeriodo())).isEmpty();
    }
}
