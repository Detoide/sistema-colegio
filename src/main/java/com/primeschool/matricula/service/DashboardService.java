package com.primeschool.matricula.service;

import com.primeschool.matricula.dto.DashboardDTO;
import com.primeschool.matricula.model.entity.Usuario;

/**
 * Servicio que construye el resumen del panel principal.
 */
public interface DashboardService {

    /**
     * Genera el resumen del dashboard para el usuario indicado.
     *
     * @param usuario usuario que consulta el panel
     * @return datos resumidos según su rol
     */
    DashboardDTO resumen(Usuario usuario);
}
