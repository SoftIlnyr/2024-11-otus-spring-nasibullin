package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Репозиторий на основе Jpa для работы с авторами")
@DataJpaTest
@Import(JpaAuthorRepository.class)
public class JpaAuthorRepositoryTest {

    @Autowired
    private JpaAuthorRepository jpaAuthorRepository;

    @Autowired
    private TestEntityManager entityManager;

    @DisplayName("Должен найти 3 автора из data.sql")
    @Test
    void shouldFindAllAuthors() {
        assertEquals(3, jpaAuthorRepository.findAll().size());
    }

    @DisplayName("Должен найти автора по id")
    @Test
    void shouldFindAuthorById() {
        long authorId = 1L;
        Author expected = entityManager.find(Author.class, authorId);
        Author actual = jpaAuthorRepository.findById(authorId).get();
        assertAll("Проверка полей авторов",
                () -> assertEquals(expected.getFullName(), actual.getFullName()),
                () -> assertTrue(actual.getBooks().containsAll(expected.getBooks()))
        );
    }

}
