package ru.otus.hw.view;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class GenreViewController {

    @GetMapping(path = "/genres")
    @RateLimiter(name = "defaultRateLimiter")
    public String findAllGenres() {
        return "genres";
    }
}
