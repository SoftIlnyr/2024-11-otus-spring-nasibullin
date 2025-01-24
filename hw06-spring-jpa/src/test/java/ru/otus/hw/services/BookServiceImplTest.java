package ru.otus.hw.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional(propagation = Propagation.NEVER)
@DisplayName("Сервис для работы с книгами")
@SpringBootTest
class BookServiceImplTest {

    @Autowired
    private BookService bookService;

    @PersistenceContext
    private EntityManager entityManager;


    @Test
    void findById() {
        long bookId = 1L;
        Book expectedBook = entityManager.find(Book.class, bookId);
        Optional<Book> actualBook = bookService.findById(bookId);
        assertTrue(actualBook.isPresent());
        checkBooksEqual(expectedBook, actualBook.get());
    }

    @Test
    void findAll() {
        List<Book> books = bookService.findAll();
        assertEquals(3, books.size());
    }

    @Test
    void insert() {
        String title = "Title";
        long authorId = 1L;
        Long genreId1 = 1L;
        Long genreId2 = 2L;
        Set<Long> genres = Set.of(genreId1, genreId2);
        Book savedBook = bookService.insert(title, authorId, genres);
        Book dbBook = entityManager.find(Book.class, savedBook.getId());
        assertNotNull(dbBook);
        Assertions.assertAll("Check saved book fields",
                () -> assertEquals(title, dbBook.getTitle()),
                () -> assertEquals(authorId, dbBook.getAuthor().getId())
        );
    }

    @Test
    void update() {
        long bookId = 1L;
        Optional<Book> book = bookService.findById(bookId);
        assertTrue(book.isPresent());

        String title = "Title";
        assertNotEquals(title, book.get().getTitle());
        long authorId = 1L;
        Long genreId1 = 1L;
        Long genreId2 = 2L;
        Set<Long> genres = Set.of(genreId1, genreId2);

        Book savedBook = bookService.update(bookId, title, authorId, genres);
        Book dbBook = entityManager.find(Book.class, savedBook.getId());
        assertNotNull(dbBook);
        Assertions.assertAll("Check saved book fields",
                () -> assertEquals(title, dbBook.getTitle()),
                () -> assertEquals(authorId, dbBook.getAuthor().getId())
        );
    }

    @Test
    void deleteById() {
        long bookId = 1L;
        Optional<Book> book = bookService.findById(bookId);
        assertTrue(book.isPresent());

        bookService.deleteById(bookId);

        Book dbBook = entityManager.find(Book.class, bookId);
        assertNull(dbBook);
    }

    void checkBooksEqual(Book expectedBook, Book actualBook) {
        assertAll("Проверка по полям",
                () -> assertEquals(expectedBook.getTitle(), actualBook.getTitle()),
                () -> assertEquals(expectedBook.getAuthor().getId(), actualBook.getAuthor().getId())
        );
    }
}