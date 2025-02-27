package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import ru.otus.hw.dto.AuthorDto;

public interface AuthorService {
    Flux<AuthorDto> findAll();
}
