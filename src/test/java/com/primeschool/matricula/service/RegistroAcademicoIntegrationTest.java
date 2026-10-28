package com.primeschool.matricula.service;

import com.primeschool.matricula.dto.RegistroAcademicoFilaDTO;
import com.primeschool.matricula.dto.RegistroAcademicoForm;
import com.primeschool.matricula.exception.ReglaNegocioException;
import com.primeschool.matricula.model.entity.Matricula;
import com.primeschool.matricula.model.entity.PeriodoAcademico;
import com.primeschool.matricula.model.entity.Usuario;
import com.primeschool.matricula.model.enums.EstadoAsistencia;
import com.primeschool.matricula.repository.AsistenciaRepository;
import com.primeschool.matricula.repository.MatriculaRepository;
import com.primeschool.matricula.repository.NotaRepository;
import com.primeschool.matricula.repository.PeriodoAcademicoRepository;
import com.primeschool.matricula.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class RegistroAcademicoIntegrationTest {

    @Autowired
    private RegistroAcademicoService registroAcademicoService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private MatriculaRepository matriculaRepository;
    @Autowired
    private PeriodoAcademicoRepository periodoRepository;
    @Autowired
    private NotaRepository notaRepository;
    @Autowired
    private AsistenciaRepository asistenciaRepository;

    private Usuario docente;
    private Matricula matricula;
    private PeriodoAcademico bimestre;

    @BeforeEach
    void prepararDatos() {
        docente = usuarioRepository.findByCorreo("docente@colegio.test").orElseThrow();
        matricula = matriculaRepository.findById(2).orElseThrow();
        bimestre = periodoRepository.findAllByOrderByFechaInicioAsc().stream()
                .filter(p -> !p.getIdPeriodo().equals(1))
                .findFirst()
                .orElseThrow();
    }

    @Test
    void guardaNotaYAsistenciaEnUnaSolaPlanilla() {
        RegistroAcademicoForm form = formulario("16.50", EstadoAsistencia.TARDANZA.name());

        registroAcademicoService.guardar(form, docente);

        assertThat(notaRepository.findByMatricula_IdMatriculaAndCurso_IdCursoAndPeriodo_IdPeriodo(
                matricula.getIdMatricula(), 1, bimestre.getIdPeriodo()))
                .get()
                .extracting("calificacion")
                .isEqualTo(new BigDecimal("16.50"));
        assertThat(asistenciaRepository.findByMatricula_IdMatriculaAndFecha(
                matricula.getIdMatricula(), LocalDate.of(2026, 3, 18)))
                .get()
                .extracting("estado")
                .isEqualTo(EstadoAsistencia.TARDANZA);
    }

    @Test
    void revierteLaNotaSiLaAsistenciaEsInvalida() {
        RegistroAcademicoForm form = formulario("12.00", "INVALIDO");

        assertThatThrownBy(() -> registroAcademicoService.guardar(form, docente))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("Estado de asistencia inválido");

        TestTransaction.end();
        TestTransaction.start();

        assertThat(notaRepository.findByMatricula_IdMatriculaAndCurso_IdCursoAndPeriodo_IdPeriodo(
                matricula.getIdMatricula(), 1, bimestre.getIdPeriodo())).isEmpty();
        assertThat(asistenciaRepository.findByMatricula_IdMatriculaAndFecha(
                matricula.getIdMatricula(), LocalDate.of(2026, 3, 18))).isEmpty();
    }

    @Test
    void planillaCombinaNotaYEstadoDeAsistencia() {
        RegistroAcademicoForm form = formulario("15.00", EstadoAsistencia.ASISTIO.name());
        registroAcademicoService.guardar(form, docente);

        List<RegistroAcademicoFilaDTO> filas = registroAcademicoService.planilla(
                matricula.getSeccion().getIdSeccion(), bimestre.getIdPeriodo(), 1,
                LocalDate.of(2026, 3, 18), docente);

        RegistroAcademicoFilaDTO fila = filas.stream()
                .filter(f -> f.getMatriculaId().equals(matricula.getIdMatricula()))
                .findFirst()
                .orElseThrow();

        assertThat(fila.getNotaActual()).isEqualByComparingTo("15.00");
        assertThat(fila.getEstadoActual()).isEqualTo(EstadoAsistencia.ASISTIO);
    }

    @Test
    void rechazaFechaFueraDelPeriodoAnualDelBimestre() {
        RegistroAcademicoForm form = formulario("15.00", EstadoAsistencia.ASISTIO.name());
        form.setFecha(LocalDate.of(2027, 3, 18));

        assertThatThrownBy(() -> registroAcademicoService.guardar(form, docente))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("periodo anual");
    }

    private RegistroAcademicoForm formulario(String nota, String estado) {
        RegistroAcademicoForm form = new RegistroAcademicoForm();
        form.setSeccionId(matricula.getSeccion().getIdSeccion());
        form.setPeriodoId(bimestre.getIdPeriodo());
        form.setCursoId(1);
        form.setFecha(LocalDate.of(2026, 3, 18));
        form.getNotas().put(matricula.getIdMatricula(), new BigDecimal(nota));
        form.getEstados().put(matricula.getIdMatricula(), estado);
        return form;
    }
}
