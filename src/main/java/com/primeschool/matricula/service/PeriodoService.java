package com.primeschool.matricula.service;

import com.primeschool.matricula.model.entity.PeriodoAcademico;

import java.time.LocalDate;
import java.util.List;

public interface PeriodoService {

    List<PeriodoAcademico> listar();

    PeriodoAcademico obtener(Integer id);

    PeriodoAcademico periodoAnualQueContiene(LocalDate fecha);

    PeriodoAcademico periodoAnualPara(PeriodoAcademico periodo);

    boolean esAnual(PeriodoAcademico periodo);

    void crear(String nombre, LocalDate fechaInicio, LocalDate fechaFin);
}
