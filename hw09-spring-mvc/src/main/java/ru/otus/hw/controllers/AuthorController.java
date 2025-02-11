package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.otus.hw.services.AuthorService;

@RequiredArgsConstructor
@Controller
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping(path = "/authors")
    public ModelAndView findAllAuthors() {

        ModelAndView modelAndView = new ModelAndView("authors");

        modelAndView.addObject("authors", authorService.findAll());

        return modelAndView;
    }
}
