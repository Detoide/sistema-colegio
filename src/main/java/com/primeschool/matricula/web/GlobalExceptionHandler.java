package com.primeschool.matricula.web;

import com.primeschool.matricula.exception.RecursoNoEncontradoException;
import com.primeschool.matricula.exception.ReglaNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ReglaNegocioException.class)
    public ModelAndView reglaNegocio(ReglaNegocioException ex) {
        ModelAndView mav = new ModelAndView("error/negocio");
        mav.addObject("mensaje", ex.getMessage());
        mav.setStatus(HttpStatus.BAD_REQUEST);
        return mav;
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ModelAndView noEncontrado(RecursoNoEncontradoException ex) {
        ModelAndView mav = new ModelAndView("error/negocio");
        mav.addObject("mensaje", ex.getMessage());
        mav.setStatus(HttpStatus.NOT_FOUND);
        return mav;
    }
}
