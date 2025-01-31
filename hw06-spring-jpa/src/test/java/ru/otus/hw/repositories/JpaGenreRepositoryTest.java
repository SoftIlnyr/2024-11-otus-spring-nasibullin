package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DisplayName("Репозиторий на основе Jpa для работы с жанрами")
@DataJpaTest
@Import(JpaGenreRepository.class)
public class JpaGenreRepositoryTest {

    @Autowired
    private JpaGenreRepository jpaGenreRepository;

    @Autowired
    private TestEntityManager entityManager;

    @DisplayName("Должен найти 6 жанров из data.sql")
    @Test
    void shouldFindAllGenres() {
        assertEquals(6, jpaGenreRepository.findAll().size());
    }

    @DisplayName("Должен найти жанры по их id")
    @Test
    void shouldFindGenresByIds() {
        long genreId1 = 1L;
        long genreId2 = 2L;
        Genre expectedGenre1 = entityManager.find(Genre.class, genreId1);
        Genre expectedGenre2 = entityManager.find(Genre.class, genreId2);

        List<Genre> actualGenres = jpaGenreRepository.findAllByIds(Set.of(genreId1, genreId2));

        assertTrue(actualGenres.containsAll(List.of(expectedGenre1, expectedGenre2)));
    }
}
