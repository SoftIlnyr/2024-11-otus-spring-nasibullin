package ru.otus.hw.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import ru.otus.hw.exceptions.EntityNotFoundException;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ModelAndView handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error(ex);
        ModelAndView modelAndView = new ModelAndView("not_found");
        modelAndView.addObject("error_message", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleEntityNotFoundException(Exception ex) {
        log.error(ex);
        ModelAndView modelAndView = new ModelAndView("server_error");
        modelAndView.addObject("error_message", ex.getMessage());
        return modelAndView;
    }
}
