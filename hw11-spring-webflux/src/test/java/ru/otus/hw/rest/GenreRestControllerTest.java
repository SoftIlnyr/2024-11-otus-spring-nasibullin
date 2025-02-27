package ru.otus.hw.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import ru.otus.hw.dto.GenreDto;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.otus.hw.mongock.changelog.TestValues.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GenreRestControllerTest {

    @LocalServerPort
    private int port;

    private WebClient webClient;

    private GenreDto genreDto1 = new GenreDto(GENRE_1.getId(), GENRE_1.getName());

    private GenreDto genreDto2 = new GenreDto(GENRE_2.getId(), GENRE_2.getName());

    private GenreDto genreDto3 = new GenreDto(GENRE_3.getId(), GENRE_3.getName());

    private GenreDto genreDto4 = new GenreDto(GENRE_4.getId(), GENRE_4.getName());

    private GenreDto genreDto5 = new GenreDto(GENRE_5.getId(), GENRE_5.getName());

    @BeforeEach
    void setUp() {
        webClient = WebClient.create(String.format("http://localhost:%d", port));
    }

    @Test
    void findAllGenres() {
        var expectedSize = GENRES.size();

        List<GenreDto> result = webClient
                .get().uri("/api/genres")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(GenreDto.class)
                .take(expectedSize)
                .timeout(Duration.ofSeconds(3))
                .collectList()
                .block();

        assertThat(result).hasSize(expectedSize)
                .contains(genreDto1, genreDto2, genreDto3, genreDto4, genreDto5);
    }
}