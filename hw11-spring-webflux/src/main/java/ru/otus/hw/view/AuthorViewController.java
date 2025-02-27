package ru.otus.hw.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Controller
public class AuthorViewController {

    @GetMapping(path = "/authors")
    public Mono<String> findAllAuthors() {
        return Mono.just("authors");
    }
}
