package ru.otus.hw.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class AuthorViewController {

    @GetMapping(path = "/authors")
    public String findAllAuthors() {

        return "authors";
    }
}
