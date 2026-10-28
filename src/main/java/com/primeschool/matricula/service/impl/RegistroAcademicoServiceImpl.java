package com.primeschool.matricula.service.impl;

import com.primeschool.matricula.dto.AsistenciaFilaDTO;
import com.primeschool.matricula.dto.AsistenciaForm;
import com.primeschool.matricula.dto.NotaFilaDTO;
import com.primeschool.matricula.dto.NotaForm;
import com.primeschool.matricula.dto.RegistroAcademicoFilaDTO;
import com.primeschool.matricula.dto.RegistroAcademicoForm;
import com.primeschool.matricula.exception.ReglaNegocioException;
import com.primeschool.matricula.model.entity.PeriodoAcademico;
import com.primeschool.matricula.model.entity.Usuario;
import com.primeschool.matricula.model.enums.EstadoAsistencia;
import com.primeschool.matricula.service.AsistenciaService;
import com.primeschool.matricula.service.NotaService;
import com.primeschool.matricula.service.PeriodoService;
import com.primeschool.matricula.service.RegistroAcademicoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegistroAcademicoServiceImpl implements RegistroAcademicoService {

    private final NotaService notaService;
    private final AsistenciaService asistenciaService;
    private final PeriodoService periodoService;

    public RegistroAcademicoServiceImpl(NotaService notaService,
                                        AsistenciaService asistenciaService,
                                        PeriodoService periodoService) {
        this.notaService = notaService;
        this.asistenciaService = asistenciaService;
        this.periodoService = periodoService;
    }

    @Override
    public List<RegistroAcademicoFilaDTO> planilla(Integer seccionId,
                                                   Integer periodoId,
                                                   Integer cursoId,
                                                   LocalDate fecha,
                                                   Usuario usuario) {
        if (seccionId == null || periodoId == null || cursoId == null || fecha == null) {
            return List.of();
        }
        validarBimestreYFecha(periodoId, fecha);

        List<NotaFilaDTO> notas = notaService.planilla(seccionId, periodoId, cursoId, usuario);
        Map<Integer, EstadoAsistencia> asistencias = new HashMap<>();
        for (AsistenciaFilaDTO asistencia : asistenciaService.planilla(seccionId, fecha, usuario)) {
            asistencias.put(asistencia.getMatriculaId(), asistencia.getEstadoActual());
        }

        return notas.stream()
                .map(nota -> new RegistroAcademicoFilaDTO(
                        nota.getMatriculaId(),
                        nota.getCodigo(),
                        nota.getEstudiante(),
                        nota.getNotaActual(),
                        asistencias.get(nota.getMatriculaId())))
                .toList();
    }

    @Override
    @Transactional
    public void guardar(RegistroAcademicoForm form, Usuario usuario) {
        if (form == null || form.getSeccionId() == null || form.getPeriodoId() == null
                || form.getCursoId() == null || form.getFecha() == null) {
            throw new ReglaNegocioException("Debe seleccionar sección, bimestre, curso y fecha");
        }
        validarBimestreYFecha(form.getPeriodoId(), form.getFecha());

        NotaForm notaForm = new NotaForm();
        notaForm.setSeccionId(form.getSeccionId());
        notaForm.setPeriodoId(form.getPeriodoId());
        notaForm.setCursoId(form.getCursoId());
        if (form.getNotas() != null) {
            notaForm.getNotas().putAll(form.getNotas());
        }
        notaService.guardar(notaForm, usuario);

        AsistenciaForm asistenciaForm = new AsistenciaForm();
        asistenciaForm.setSeccionId(form.getSeccionId());
        asistenciaForm.setFecha(form.getFecha());
        if (form.getEstados() != null) {
            asistenciaForm.getEstados().putAll(form.getEstados());
        }
        asistenciaService.guardar(asistenciaForm, usuario);
    }

    private void validarBimestreYFecha(Integer periodoId, LocalDate fecha) {
        PeriodoAcademico periodo = periodoService.obtener(periodoId);
        if (periodoService.esAnual(periodo)) {
            throw new ReglaNegocioException("Seleccione un bimestre para registrar notas y asistencia");
        }
        PeriodoAcademico anual = periodoService.periodoAnualPara(periodo);
        if (!anual.contiene(fecha)) {
            throw new ReglaNegocioException(
                    "La fecha seleccionada no pertenece al periodo anual del bimestre");
        }
    }
}
