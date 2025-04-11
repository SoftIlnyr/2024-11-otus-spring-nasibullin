package ru.otus.hw.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class AuthorRestController {

    private final AuthorService authorService;

    @GetMapping(path = "/api/authors")
    public ResponseEntity<List<AuthorDto>> findAllAuthors() {

        List<AuthorDto> authors = authorService.findAll();

        return ResponseEntity.ok(authors);
    }
}
