package ru.otus.hw.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.otus.hw.services.GenreService;

@RequiredArgsConstructor
@Controller
public class GenreViewController {

    private final GenreService genreService;

    @GetMapping(path = "/genres")
    public ModelAndView findAllAuthors() {

        ModelAndView modelAndView = new ModelAndView("genres");

        modelAndView.addObject("genres", genreService.findAll());

        return modelAndView;
    }
}
