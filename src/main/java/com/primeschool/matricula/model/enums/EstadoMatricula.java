package com.primeschool.matricula.model.enums;

/**
 * Estados posibles de una matrícula.
 */
public enum EstadoMatricula {
    /** Matrícula registrada pendiente de confirmación. */
    PENDIENTE,
    /** Matrícula confirmada y vigente. */
    CONFIRMADA,
    /** Matrícula anulada, con liberación de la vacante. */
    ANULADA
}
