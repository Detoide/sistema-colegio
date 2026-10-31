package com.primeschool.matricula.exception;

/**
 * Excepción lanzada cuando no se encuentra un recurso solicitado.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    /**
     * Crea la excepción con el mensaje descriptivo del recurso ausente.
     *
     * @param mensaje detalle del recurso no encontrado
     */
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
