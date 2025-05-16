package ru.otus.hw.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mappers.AuthorMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.Collections;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;

    @PreAuthorize("hasRole(T(ru.otus.hw.security.UserRole).READER)")
    @RateLimiter(name = "defaultRateLimiter")
    @CircuitBreaker(name = "defaultCircuitBreaker", fallbackMethod = "findAllFallback")
    @Override
    public List<AuthorDto> findAll() {
        List<Author> authorService = authorRepository.findAll();
        return authorMapper.toDto(authorService);
    }

    private List<AuthorDto> findAllFallback(Exception exception) {
        log.error("CircuitBreaker triggered due to: " + exception.getMessage());
        return Collections.emptyList();
    }

    @PreAuthorize("hasRole(T(ru.otus.hw.security.UserRole).READER)")
    @RateLimiter(name = "defaultRateLimiter")
    @CircuitBreaker(name = "defaultCircuitBreaker")
    public AuthorDto findById(String id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %s not found".formatted(id)));
        return authorMapper.toDto(author);
    }
}
