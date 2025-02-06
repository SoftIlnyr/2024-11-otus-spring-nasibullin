package ru.otus.hw.repositories;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Book;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Репозиторий на основе Jpa для работы с книгами")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager entityManager;

    @DisplayName("Должен загружать информацию о книге по ее id")
    @Test
    void shouldFindExpectedBookById() {
        long bookId = 1L;
        var optionalBook = bookRepository.findById(bookId);
        var expectedBook = entityManager.find(Book.class, bookId);
        assertTrue(optionalBook.isPresent());
        Assertions.assertThat(optionalBook.get()).usingRecursiveComparison().isEqualTo(expectedBook);
    }

}
