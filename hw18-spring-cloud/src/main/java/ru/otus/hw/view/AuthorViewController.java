package ru.otus.hw.view;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class AuthorViewController {

    @GetMapping(path = "/authors")
    @RateLimiter(name = "defaultRateLimiter")
    public String findAllAuthors() {
        return "authors";
    }
}
