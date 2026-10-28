package com.primeschool.matricula.service;

import com.primeschool.matricula.dto.DashboardDTO;
import com.primeschool.matricula.model.entity.Usuario;

public interface DashboardService {

    DashboardDTO resumen(Usuario usuario);
}
