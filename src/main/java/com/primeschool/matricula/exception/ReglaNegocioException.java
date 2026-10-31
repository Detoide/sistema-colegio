package com.primeschool.matricula.exception;

/**
 * Excepción lanzada cuando una operación incumple una regla de negocio.
 */
public class ReglaNegocioException extends RuntimeException {

    /**
     * Crea la excepción con el mensaje descriptivo del incumplimiento.
     *
     * @param mensaje detalle de la regla de negocio incumplida
     */
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
