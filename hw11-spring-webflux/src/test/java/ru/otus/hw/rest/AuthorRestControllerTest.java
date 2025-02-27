package ru.otus.hw.rest;

import com.github.cloudyrock.spring.v5.EnableMongock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.mongock.changelog.InitTestChangelog;
import ru.otus.hw.mongock.changelog.TestValues;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static ru.otus.hw.mongock.changelog.TestValues.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableMongock
class AuthorRestControllerTest {

    @LocalServerPort
    private int port;

    private WebClient webClient;

    private final AuthorDto authorDto1 = new AuthorDto(AUTHOR_1.getId(), AUTHOR_1.getFullName());

    private final AuthorDto authorDto2 = new AuthorDto(AUTHOR_2.getId(), AUTHOR_2.getFullName());

    private final AuthorDto authorDto3 = new AuthorDto(AUTHOR_3.getId(), AUTHOR_3.getFullName());

    private final AuthorDto authorDto4 = new AuthorDto(AUTHOR_4.getId(), AUTHOR_4.getFullName());


    @BeforeEach
    void setUp() {
        webClient = WebClient.create(String.format("http://localhost:%d", port));
    }

    @Test
    void findAllAuthors() {
        var expectedSize = AUTHORS.size();

        List<AuthorDto> result = webClient
                .get().uri("/api/authors")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(AuthorDto.class)
                .take(expectedSize)
                .timeout(Duration.ofSeconds(3))
                .collectList()
                .block();

        assertThat(result)
                .hasSize(expectedSize)
                .contains(authorDto1, authorDto2, authorDto3, authorDto4);
    }

}