package com.primeschool.matricula.service;

import com.primeschool.matricula.dto.RegistroAcademicoFilaDTO;
import com.primeschool.matricula.dto.RegistroAcademicoForm;
import com.primeschool.matricula.model.entity.Usuario;

import java.time.LocalDate;
import java.util.List;

public interface RegistroAcademicoService {

    List<RegistroAcademicoFilaDTO> planilla(Integer seccionId,
                                            Integer periodoId,
                                            Integer cursoId,
                                            LocalDate fecha,
                                            Usuario usuario);

    void guardar(RegistroAcademicoForm form, Usuario usuario);
}
