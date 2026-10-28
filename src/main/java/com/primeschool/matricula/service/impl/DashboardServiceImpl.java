package com.primeschool.matricula.service.impl;

import com.primeschool.matricula.dto.DashboardDTO;
import com.primeschool.matricula.model.entity.Matricula;
import com.primeschool.matricula.model.entity.Seccion;
import com.primeschool.matricula.model.entity.Usuario;
import com.primeschool.matricula.repository.AlertaTempranaRepository;
import com.primeschool.matricula.repository.EstudianteRepository;
import com.primeschool.matricula.repository.MatriculaRepository;
import com.primeschool.matricula.repository.SeccionRepository;
import com.primeschool.matricula.service.DashboardService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final EstudianteRepository estudianteRepository;
    private final SeccionRepository seccionRepository;
    private final MatriculaRepository matriculaRepository;
    private final AlertaTempranaRepository alertaRepository;

    public DashboardServiceImpl(EstudianteRepository estudianteRepository,
                                SeccionRepository seccionRepository,
                                MatriculaRepository matriculaRepository,
                                AlertaTempranaRepository alertaRepository) {
        this.estudianteRepository = estudianteRepository;
        this.seccionRepository = seccionRepository;
        this.matriculaRepository = matriculaRepository;
        this.alertaRepository = alertaRepository;
    }

    @Override
    public DashboardDTO resumen(Usuario usuario) {
        DashboardDTO dto = new DashboardDTO();
        String rol = usuario.getRol().getNombreRol().toUpperCase();
        dto.setNombreUsuario(usuario.getNombreCompleto());
        dto.setRol(rol);

        switch (rol) {
            case "ADMINISTRADOR", "DIRECTIVO" -> {
                dto.setEstudiantes(estudianteRepository.count());
                dto.setSecciones(seccionRepository.count());
                dto.setMatriculas(matriculaRepository.count());
                dto.setAlertasActivas(alertaRepository.countByAtendidaFalse());
            }
            case "DOCENTE" -> {
                List<Seccion> secciones = seccionRepository
                        .findByTutor_IdUsuarioOrderByGrado_NombreGradoAscNombreSeccionAsc(usuario.getIdUsuario());
                List<Matricula> matriculas = matriculaRepository
                        .findBySeccion_Tutor_IdUsuarioOrderByPeriodo_FechaInicioDescEstudiante_ApellidosAsc(
                                usuario.getIdUsuario()).stream()
                        .filter(m -> m.getEstado() != com.primeschool.matricula.model.enums.EstadoMatricula.ANULADA)
                        .toList();
                Set<Integer> estudiantes = matriculas.stream()
                        .map(m -> m.getEstudiante().getIdEstudiante())
                        .collect(Collectors.toSet());
                dto.setSecciones(secciones.size());
                dto.setEstudiantes(estudiantes.size());
                dto.setMatriculas(matriculas.size());
                dto.setSeccionesDocente(secciones);
                dto.setAlertas(alertaRepository
                        .findByMatricula_Seccion_Tutor_IdUsuarioAndAtendidaFalseOrderByFechaGeneradaDesc(
                                usuario.getIdUsuario()));
                dto.setAlertasActivas(dto.getAlertas().size());
            }
            case "APODERADO" -> {
                dto.setHijos(estudianteRepository
                        .findByApoderado_Usuario_IdUsuarioOrderByApellidosAscNombresAsc(usuario.getIdUsuario()));
                dto.setEstudiantes(dto.getHijos().size());
                List<Matricula> matriculas = matriculaRepository
                        .findByEstudiante_Apoderado_Usuario_IdUsuarioOrderByPeriodo_FechaInicioDescEstudiante_ApellidosAsc(
                                usuario.getIdUsuario()).stream()
                        .filter(m -> m.getEstado() != com.primeschool.matricula.model.enums.EstadoMatricula.ANULADA)
                        .toList();
                dto.setMatriculas(matriculas.size());
                dto.setAlertas(alertaRepository
                        .findByMatricula_Estudiante_Apoderado_Usuario_IdUsuarioOrderByFechaGeneradaDesc(
                                usuario.getIdUsuario()));
                dto.setAlertasActivas(dto.getAlertas().stream()
                        .filter(a -> Boolean.FALSE.equals(a.getAtendida()))
                        .count());
            }
            default -> {
            }
        }
        return dto;
    }
}
