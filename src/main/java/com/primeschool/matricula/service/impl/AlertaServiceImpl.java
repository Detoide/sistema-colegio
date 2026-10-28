package com.primeschool.matricula.service.impl;

import com.primeschool.matricula.exception.RecursoNoEncontradoException;
import com.primeschool.matricula.exception.ReglaNegocioException;
import com.primeschool.matricula.model.entity.AlertaTemprana;
import com.primeschool.matricula.model.entity.Matricula;
import com.primeschool.matricula.model.entity.Usuario;
import com.primeschool.matricula.model.enums.EstadoAsistencia;
import com.primeschool.matricula.model.enums.TipoRiesgo;
import com.primeschool.matricula.repository.AlertaTempranaRepository;
import com.primeschool.matricula.repository.AsistenciaRepository;
import com.primeschool.matricula.repository.MatriculaRepository;
import com.primeschool.matricula.repository.NotaRepository;
import com.primeschool.matricula.service.AlertaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class AlertaServiceImpl implements AlertaService {

    private final AlertaTempranaRepository alertaRepository;
    private final NotaRepository notaRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final MatriculaRepository matriculaRepository;
    private final BigDecimal umbralNota;
    private final int faltasParaAlerta;

    public AlertaServiceImpl(AlertaTempranaRepository alertaRepository,
                             NotaRepository notaRepository,
                             AsistenciaRepository asistenciaRepository,
                             MatriculaRepository matriculaRepository,
                             @Value("${app.alertas.umbral-nota:11}") BigDecimal umbralNota,
                             @Value("${app.alertas.faltas-para-alerta:3}") int faltasParaAlerta) {
        this.alertaRepository = alertaRepository;
        this.notaRepository = notaRepository;
        this.asistenciaRepository = asistenciaRepository;
        this.matriculaRepository = matriculaRepository;
        this.umbralNota = umbralNota;
        this.faltasParaAlerta = faltasParaAlerta;
    }

    @Override
    public List<AlertaTemprana> listarVisibles(Usuario usuario) {
        String rol = usuario.getRol().getNombreRol().toUpperCase();
        return switch (rol) {
            case "ADMINISTRADOR", "DIRECTIVO" -> alertaRepository.findByAtendidaFalseOrderByFechaGeneradaDesc();
            case "DOCENTE" -> alertaRepository
                    .findByMatricula_Seccion_Tutor_IdUsuarioAndAtendidaFalseOrderByFechaGeneradaDesc(
                            usuario.getIdUsuario());
            case "APODERADO" -> alertaRepository
                    .findByMatricula_Estudiante_Apoderado_Usuario_IdUsuarioOrderByFechaGeneradaDesc(
                            usuario.getIdUsuario());
            default -> List.of();
        };
    }

    @Override
    @Transactional
    public void atender(Integer idAlerta, Usuario usuario) {
        AlertaTemprana alerta = alertaRepository.findById(idAlerta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Alerta no encontrada"));
        String rol = usuario.getRol().getNombreRol().toUpperCase();
        boolean autorizado = switch (rol) {
            case "ADMINISTRADOR", "DIRECTIVO" -> true;
            case "DOCENTE" -> alerta.getMatricula().getSeccion().getTutor() != null
                    && alerta.getMatricula().getSeccion().getTutor().getIdUsuario()
                    .equals(usuario.getIdUsuario());
            default -> false;
        };
        if (!autorizado) {
            throw new ReglaNegocioException("No puede atender esta alerta");
        }
        alerta.setAtendida(true);
        alertaRepository.save(alerta);
    }

    @Override
    @Transactional
    public void evaluarRiesgoAcademico(Integer idMatricula, Integer idPeriodo) {
        if (idMatricula == null || idPeriodo == null) {
            return;
        }
        Double promedio = notaRepository.promedio(idMatricula, idPeriodo);
        if (promedio == null) {
            return;
        }
        BigDecimal valor = BigDecimal.valueOf(promedio).setScale(2, RoundingMode.HALF_UP);
        if (valor.compareTo(umbralNota) >= 0) {
            return;
        }
        if (alertaRepository.existsByMatricula_IdMatriculaAndTipoRiesgoAndAtendidaFalse(
                idMatricula, TipoRiesgo.REPROBACION)) {
            return;
        }
        Matricula matricula = matriculaRepository.findById(idMatricula).orElse(null);
        if (matricula == null) {
            return;
        }
        AlertaTemprana alerta = new AlertaTemprana();
        alerta.setMatricula(matricula);
        alerta.setTipoRiesgo(TipoRiesgo.REPROBACION);
        alerta.setDescripcion("Promedio " + valor + " por debajo del umbral " + umbralNota);
        alerta.setAtendida(false);
        alertaRepository.save(alerta);
    }

    @Override
    @Transactional
    public void evaluarInasistencia(Integer idMatricula) {
        if (idMatricula == null) {
            return;
        }
        long faltas = asistenciaRepository.countByMatricula_IdMatriculaAndEstado(
                idMatricula, EstadoAsistencia.FALTA);
        if (faltas < faltasParaAlerta) {
            return;
        }
        if (alertaRepository.existsByMatricula_IdMatriculaAndTipoRiesgoAndAtendidaFalse(
                idMatricula, TipoRiesgo.INASISTENCIA)) {
            return;
        }
        Matricula matricula = matriculaRepository.findById(idMatricula).orElse(null);
        if (matricula == null) {
            return;
        }
        AlertaTemprana alerta = new AlertaTemprana();
        alerta.setMatricula(matricula);
        alerta.setTipoRiesgo(TipoRiesgo.INASISTENCIA);
        alerta.setDescripcion("El estudiante acumula " + faltas + " faltas");
        alerta.setAtendida(false);
        alertaRepository.save(alerta);
    }

    @Override
    public long contarActivas() {
        return alertaRepository.countByAtendidaFalse();
    }
}
