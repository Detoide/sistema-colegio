package com.primeschool.matricula.service;

import com.primeschool.matricula.dto.AsistenciaForm;
import com.primeschool.matricula.dto.BoletaDTO;
import com.primeschool.matricula.dto.MatriculaRequest;
import com.primeschool.matricula.dto.NotaForm;
import com.primeschool.matricula.exception.ReglaNegocioException;
import com.primeschool.matricula.model.entity.Apoderado;
import com.primeschool.matricula.model.entity.Curso;
import com.primeschool.matricula.model.entity.Estudiante;
import com.primeschool.matricula.model.entity.Grado;
import com.primeschool.matricula.model.entity.Matricula;
import com.primeschool.matricula.model.entity.PeriodoAcademico;
import com.primeschool.matricula.model.entity.Rol;
import com.primeschool.matricula.model.entity.Seccion;
import com.primeschool.matricula.model.entity.Usuario;
import com.primeschool.matricula.model.enums.EstadoAsistencia;
import com.primeschool.matricula.model.enums.TipoRiesgo;
import com.primeschool.matricula.repository.AlertaTempranaRepository;
import com.primeschool.matricula.repository.ApoderadoRepository;
import com.primeschool.matricula.repository.AsistenciaRepository;
import com.primeschool.matricula.repository.CursoRepository;
import com.primeschool.matricula.repository.EstudianteRepository;
import com.primeschool.matricula.repository.GradoRepository;
import com.primeschool.matricula.repository.NotaRepository;
import com.primeschool.matricula.repository.PeriodoAcademicoRepository;
import com.primeschool.matricula.repository.RolRepository;
import com.primeschool.matricula.repository.SeccionRepository;
import com.primeschool.matricula.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class SeguimientoAcademicoIntegrationTest {

    @Autowired
    private MatriculaService matriculaService;
    @Autowired
    private NotaService notaService;
    @Autowired
    private AsistenciaService asistenciaService;
    @Autowired
    private BoletaService boletaService;
    @Autowired
    private PeriodoService periodoService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RolRepository rolRepository;
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
    private CursoRepository cursoRepository;
    @Autowired
    private NotaRepository notaRepository;
    @Autowired
    private AsistenciaRepository asistenciaRepository;
    @Autowired
    private AlertaTempranaRepository alertaRepository;

    private Usuario docente;
    private Usuario admin;
    private Usuario apoderadoUsuario;
    private PeriodoAcademico anual;
    private PeriodoAcademico bimestre;
    private Seccion seccion;
    private Estudiante estudiante;
    private Matricula matricula;
    private Curso primerCurso;
    private Curso segundoCurso;

    @BeforeEach
    void prepararDatos() {
        docente = usuarioRepository.findByCorreo("docente@colegio.test").orElseThrow();
        admin = usuarioRepository.findByCorreo("admin@colegio.test").orElseThrow();
        apoderadoUsuario = usuarioRepository.findByCorreo("apoderado@correo.test").orElseThrow();

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
        seccion.setVacantesTotales(10);
        seccion.setVacantesDisponibles(10);
        seccionRepository.save(seccion);

        Apoderado apoderado = apoderadoRepository.findByUsuario_IdUsuario(apoderadoUsuario.getIdUsuario())
                .orElseThrow();
        estudiante = new Estudiante();
        estudiante.setApoderado(apoderado);
        estudiante.setCodigo("T" + (System.nanoTime() % 1_000_000_000L));
        estudiante.setNombres("Alumno");
        estudiante.setApellidos("Seguimiento");
        estudiante.setFechaNacimiento(LocalDate.of(2012, 5, 10));
        estudianteRepository.save(estudiante);

        MatriculaRequest request = new MatriculaRequest();
        request.setEstudianteId(estudiante.getIdEstudiante());
        request.setSeccionId(seccion.getIdSeccion());
        request.setPeriodoId(anual.getIdPeriodo());
        matricula = matriculaService.registrar(request);

        List<Curso> cursos = cursoRepository.findAllByOrderByNombreCursoAsc();
        primerCurso = cursos.get(0);
        segundoCurso = cursos.get(1);
    }

    @Test
    void registraNotaBajaYGeneraAlertaDeReprobacion() {
        notaService.guardar(notaForm(primerCurso.getIdCurso(), "10"), docente);

        assertThat(notaRepository.findByMatricula_IdMatriculaAndCurso_IdCursoAndPeriodo_IdPeriodo(
                matricula.getIdMatricula(), primerCurso.getIdCurso(), bimestre.getIdPeriodo()))
                .get()
                .extracting("calificacion")
                .isEqualTo(new BigDecimal("10"));
        assertThat(alertaRepository.existsByMatricula_IdMatriculaAndTipoRiesgoAndAtendidaFalse(
                matricula.getIdMatricula(), TipoRiesgo.REPROBACION)).isTrue();
    }

    @Test
    void noGeneraAlertaConPromedioAprobado() {
        notaService.guardar(notaForm(primerCurso.getIdCurso(), "15"), docente);

        assertThat(alertaRepository.existsByMatricula_IdMatriculaAndTipoRiesgoAndAtendidaFalse(
                matricula.getIdMatricula(), TipoRiesgo.REPROBACION)).isFalse();
    }

    @Test
    void rechazaNotaFueraDeRango() {
        assertThatThrownBy(() -> notaService.guardar(
                notaForm(primerCurso.getIdCurso(), "21"), docente))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("entre 0 y 20");
        assertThat(notaRepository.findByMatricula_IdMatriculaAndPeriodo_IdPeriodo(
                matricula.getIdMatricula(), bimestre.getIdPeriodo())).isEmpty();
    }

    @Test
    void rechazaNotasDeDocenteNoTutorYDeApoderado() {
        assertThatThrownBy(() -> notaService.guardar(
                notaForm(primerCurso.getIdCurso(), "15"), usuarioTransitorio("DOCENTE", 999_999)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("docente tutor");

        assertThatThrownBy(() -> notaService.guardar(
                notaForm(primerCurso.getIdCurso(), "15"), usuarioTransitorio("APODERADO", 999_999)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("Solo el docente");
    }

    @Test
    void generaAlertaDeInasistenciaAlTerceraFaltaYSinDuplicados() {
        LocalDate base = anual.getFechaInicio().plusDays(10);

        asistenciaService.guardar(asistenciaForm(base, EstadoAsistencia.FALTA), docente);
        asistenciaService.guardar(asistenciaForm(base.plusDays(1), EstadoAsistencia.FALTA), docente);

        assertThat(alertaRepository.existsByMatricula_IdMatriculaAndTipoRiesgoAndAtendidaFalse(
                matricula.getIdMatricula(), TipoRiesgo.INASISTENCIA)).isFalse();

        asistenciaService.guardar(asistenciaForm(base.plusDays(2), EstadoAsistencia.FALTA), docente);

        assertThat(alertaRepository.existsByMatricula_IdMatriculaAndTipoRiesgoAndAtendidaFalse(
                matricula.getIdMatricula(), TipoRiesgo.INASISTENCIA)).isTrue();

        asistenciaService.guardar(asistenciaForm(base.plusDays(2), EstadoAsistencia.FALTA), docente);

        assertThat(asistenciaRepository.countByMatricula_IdMatriculaAndEstado(
                matricula.getIdMatricula(), EstadoAsistencia.FALTA)).isEqualTo(3);
    }

    @Test
    void rechazaAsistenciaDeDocenteNoTutorYDeApoderado() {
        LocalDate fecha = anual.getFechaInicio().plusDays(10);

        assertThatThrownBy(() -> asistenciaService.guardar(
                asistenciaForm(fecha, EstadoAsistencia.ASISTIO), usuarioTransitorio("DOCENTE", 999_999)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("docente tutor");

        assertThatThrownBy(() -> asistenciaService.guardar(
                asistenciaForm(fecha, EstadoAsistencia.ASISTIO), usuarioTransitorio("APODERADO", 999_999)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("Solo el docente");
    }

    @Test
    void generaBoletaConPromedioYControlaAcceso() {
        notaService.guardar(notaForm(primerCurso.getIdCurso(), "15"), docente);
        notaService.guardar(notaForm(segundoCurso.getIdCurso(), "10"), docente);

        BoletaDTO boleta = boletaService.generar(
                matricula.getIdMatricula(), bimestre.getIdPeriodo(), admin);

        assertThat(boleta.getNotas()).hasSize(2);
        assertThat(boleta.getPromedio()).isEqualByComparingTo("12.50");
        assertThat(boleta.getEstudiante()).isEqualTo(estudiante.getNombreCompleto());

        assertThatThrownBy(() -> boletaService.generar(
                matricula.getIdMatricula(), anual.getIdPeriodo(), admin))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("bimestre");

        assertThatThrownBy(() -> boletaService.generar(
                matricula.getIdMatricula(), bimestre.getIdPeriodo(),
                usuarioTransitorio("APODERADO", 999_999)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("acceso");

        BoletaDTO boletaApoderado = boletaService.generar(
                matricula.getIdMatricula(), bimestre.getIdPeriodo(), apoderadoUsuario);

        assertThat(boletaApoderado.getCodigo()).isEqualTo(estudiante.getCodigo());
    }

    private NotaForm notaForm(Integer cursoId, String valor) {
        NotaForm form = new NotaForm();
        form.setSeccionId(seccion.getIdSeccion());
        form.setPeriodoId(bimestre.getIdPeriodo());
        form.setCursoId(cursoId);
        form.getNotas().put(matricula.getIdMatricula(), new BigDecimal(valor));
        return form;
    }

    private AsistenciaForm asistenciaForm(LocalDate fecha, EstadoAsistencia estado) {
        AsistenciaForm form = new AsistenciaForm();
        form.setSeccionId(seccion.getIdSeccion());
        form.setFecha(fecha);
        form.getEstados().put(matricula.getIdMatricula(), estado.name());
        return form;
    }

    private Usuario usuarioTransitorio(String rol, int id) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id);
        Rol rolEntidad = rolRepository.findByNombreRol(rol).orElseThrow();
        usuario.setRol(rolEntidad);
        usuario.setNombres("Usuario");
        usuario.setApellidos("Transitorio");
        usuario.setCorreo("transitorio-" + id + "@test.local");
        usuario.setContrasenaHash("no-usada");
        usuario.setActivo(true);
        return usuario;
    }
}
