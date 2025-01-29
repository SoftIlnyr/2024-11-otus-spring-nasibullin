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

@DisplayName("Репозиторий на основе Jpa для работы с книгами")
@DataJpaTest
@Import(JpaBookRepository.class)
public class JpaBookRepositoryTest {

    @Autowired
    private JpaBookRepository jpaBookRepository;

    @Autowired
    private TestEntityManager entityManager;

    @DisplayName("Должен загружать информацию о книге по ее id")
    @Test
    void shouldFindExpectedBookById() {
        long bookId = 1L;
        var optionalBook = jpaBookRepository.findById(bookId);
        var expectedBook = entityManager.find(Book.class, bookId);
        assertTrue(optionalBook.isPresent());
        checkBooksEqual(expectedBook, optionalBook.get());
    }

    @DisplayName("Должен найти 3 книги, загруженные через data.sql")
    @Test
    void shouldFindAllBooks() {
        assertEquals(3, jpaBookRepository.findAll().size());
    }

    @DisplayName("Должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        long authorId = 1L;
        long genreId = 1L;
        String bookTitle = "BookTitle_Test";
        Author author = entityManager.find(Author.class, authorId);
        Genre genre = entityManager.find(Genre.class, genreId);
        Book newBook = new Book();
        newBook.setAuthor(author);
        newBook.setGenres(Collections.singletonList(genre));
        newBook.setTitle(bookTitle);
        Book savedBook = jpaBookRepository.save(newBook);
        assertNotNull(savedBook);
        var dbBook = entityManager.find(Book.class, savedBook.getId());
        checkBooksEqual(dbBook, newBook);
    }

    @DisplayName("Должен обновлять книгу")
    @Test
    void shouldUpdateBook() {
        long bookId = 1L;
        long authorId = 2L;
        long genreId = 6L;
        String bookTitle = "BookTitle_Test";

        Author author = entityManager.find(Author.class, authorId);
        Genre genre = entityManager.find(Genre.class, genreId);
        Book updateBook = new Book(bookId, bookTitle, author, Collections.singletonList(genre));
        Book savedBook = jpaBookRepository.save(updateBook);
        assertNotNull(savedBook);
        var dbBook = entityManager.find(Book.class, savedBook.getId());
        checkBooksEqual(dbBook, updateBook);
    }

    @DisplayName("Должен удалять книгу")
    @Test
    void shouldDeleteBook() {
        long bookId = 1L;

        jpaBookRepository.deleteById(bookId);
        var dbBook = entityManager.find(Book.class, bookId);
        assertNull(dbBook);
    }

    void checkBooksEqual(Book expectedBook, Book actualBook) {
        var expectedBookGenres = expectedBook.getGenres().stream().map(Genre::getId).toList();
        var actualBookGenres = actualBook.getGenres().stream().map(Genre::getId).toList();
        assertAll("Проверка по полям",
                () -> assertEquals(expectedBook.getTitle(), actualBook.getTitle()),
                () -> assertEquals(expectedBook.getAuthor().getId(), actualBook.getAuthor().getId()),
                () -> assertTrue(actualBookGenres.containsAll(expectedBookGenres))
        );
    }
}
