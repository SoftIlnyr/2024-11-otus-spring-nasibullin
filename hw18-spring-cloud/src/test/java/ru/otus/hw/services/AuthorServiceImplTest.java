package ru.otus.hw.services;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.mappers.AuthorMapper;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DisplayName("Сервис для работы с авторами книг")
@SpringBootTest
@WithMockUser(roles = "ADMIN") //заглушка
class AuthorServiceImplTest {

    @Autowired
    private AuthorService authorService;

    @MockitoBean
    private AuthorRepository authorRepository;

    @Autowired
    private AuthorMapper authorMapper;

    private CircuitBreaker circuitBreaker;

    @MockitoBean
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @BeforeEach
    void setUp() {
        circuitBreaker = CircuitBreaker.ofDefaults("test");
        when(circuitBreakerRegistry.circuitBreaker("defaultCircuitBreaker")).thenReturn(circuitBreaker);
    }

    @Test
    void findAll_withFallback() {
        when(authorRepository.findAll()).thenThrow(new RuntimeException());
        List<AuthorDto> result = authorService.findAll();
        assertEquals(0, result.size());
    }
}