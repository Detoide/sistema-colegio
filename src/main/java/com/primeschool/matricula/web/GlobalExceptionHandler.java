package com.primeschool.matricula.web;

import com.primeschool.matricula.exception.RecursoNoEncontradoException;
import com.primeschool.matricula.exception.ReglaNegocioException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.ModelAndView;

/**
 * Maneja de forma centralizada las excepciones producidas por los controladores.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Responde a las reglas de negocio incumplidas.
     *
     * @param ex excepción de negocio
     * @return vista de error con estado 400
     */
    @ExceptionHandler(ReglaNegocioException.class)
    public ModelAndView reglaNegocio(ReglaNegocioException ex) {
        ModelAndView mav = new ModelAndView("error/negocio");
        mav.addObject("mensaje", ex.getMessage());
        mav.setStatus(HttpStatus.BAD_REQUEST);
        return mav;
    }

    /**
     * Responde cuando no se encuentra un recurso solicitado.
     *
     * @param ex excepción de recurso no encontrado
     * @return vista de error con estado 404
     */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ModelAndView noEncontrado(RecursoNoEncontradoException ex) {
        ModelAndView mav = new ModelAndView("error/negocio");
        mav.addObject("mensaje", ex.getMessage());
        mav.setStatus(HttpStatus.NOT_FOUND);
        return mav;
    }

    /**
     * Responde a los errores de validación de datos.
     *
     * @param ex excepción de validación
     * @return vista de error con estado 400
     */
    @ExceptionHandler({ConstraintViolationException.class, HandlerMethodValidationException.class})
    public ModelAndView validacion(Exception ex) {
        log.warn("Solicitud con datos inválidos: {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error/negocio");
        mav.addObject("mensaje", "Los datos enviados no son válidos. Verifique los campos e intente nuevamente.");
        mav.setStatus(HttpStatus.BAD_REQUEST);
        return mav;
    }

    /**
     * Responde a las violaciones de integridad de la base de datos.
     *
     * @param ex excepción de integridad
     * @return vista de error con estado 409
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView datosInvalidos(DataIntegrityViolationException ex) {
        log.warn("Violación de integridad de datos", ex);
        ModelAndView mav = new ModelAndView("error/negocio");
        mav.addObject("mensaje", "No se pudo guardar la información. Verifique que los datos no estén duplicados y que no superen los límites permitidos.");
        mav.setStatus(HttpStatus.CONFLICT);
        return mav;
    }

    /**
     * Responde cuando el usuario no tiene permisos para la operación.
     *
     * @param ex excepción de acceso denegado
     * @return vista de error con estado 403
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView accesoDenegado(AccessDeniedException ex) {
        ModelAndView mav = new ModelAndView("error/negocio");
        mav.addObject("mensaje", "No tiene permisos para realizar esta operación.");
        mav.setStatus(HttpStatus.FORBIDDEN);
        return mav;
    }

    /**
     * Responde a cualquier error no controlado.
     *
     * @param ex excepción inesperada
     * @return vista de error con el estado correspondiente
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView errorInesperado(Exception ex) {
        if (ex instanceof ErrorResponse errorResponse) {
            ModelAndView mav = new ModelAndView("error/negocio");
            mav.addObject("mensaje", "No se pudo procesar la solicitud.");
            mav.setStatus(errorResponse.getStatusCode());
            return mav;
        }
        log.error("Error no controlado", ex);
        ModelAndView mav = new ModelAndView("error/negocio");
        mav.addObject("mensaje", "Ocurrió un error inesperado. Intente nuevamente.");
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return mav;
    }
}
