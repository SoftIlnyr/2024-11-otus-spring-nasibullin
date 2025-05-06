package ru.otus.hw.rest;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class GenreRestController {

    private final GenreService genreService;

    @GetMapping(path = "/api/genres")
    @RateLimiter(name = "defaultRateLimiter")
    @CircuitBreaker(name = "defaultCircuitBreaker")
    public ResponseEntity<List<GenreDto>> findAllAuthors() {
        List<GenreDto> genres = genreService.findAll();

        return ResponseEntity.ok(genres);
    }
}
