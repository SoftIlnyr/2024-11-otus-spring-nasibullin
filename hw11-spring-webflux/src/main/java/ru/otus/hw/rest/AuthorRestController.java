package ru.otus.hw.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

@RequiredArgsConstructor
@RestController
public class AuthorRestController {

    private final AuthorService authorService;

    @GetMapping(path = "/api/authors")
    public Flux<AuthorDto> findAllAuthors() {
        return authorService.findAll();
    }
}
