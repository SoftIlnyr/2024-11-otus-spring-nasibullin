package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookComment;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Репозиторий на основе Jpa для работы с комментариями к книгам")
@DataJpaTest
@Import(JpaBookCommentRepository.class)
public class JpaBookCommentRepositoryTest {

    @Autowired
    private BookCommentRepository bookCommentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @DisplayName("Должен найти все 6 комментов из data.sql")
    @Test
    void shouldFindAllBookComments() {
        assertEquals(6, bookCommentRepository.findAll().size());
    }

    @DisplayName("Должен найти комментарий по его id")
    @Test
    void shouldFindBookCommentById() {
        long bookCommentId = 1L;
        BookComment expectedBookComment = entityManager.find(BookComment.class, bookCommentId);
        Optional<BookComment> bookComment = bookCommentRepository.findById(bookCommentId);
        assertTrue(bookComment.isPresent());
        BookComment actualBookComment = bookComment.get();
        checkBookCommentsEqual(expectedBookComment, actualBookComment);
    }

    @DisplayName("Должен найти комментарии к книге по id книги")
    @Test
    void shouldFindBookCommentsByBookId() {
        long bookCommentId = 1L;
        Book book = entityManager.find(Book.class, bookCommentId);
        List<BookComment> actualBookComments = bookCommentRepository.findByBookId(bookCommentId);
        assertEquals(book.getComments().size(), actualBookComments.size());
    }

    @DisplayName("Должен добавлять комментарий к книге")
    @Test
    void shouldInsertBookComment() {
        long bookId = 1L;
        String bookCommentText = "test text";
        Book book = entityManager.find(Book.class, bookId);
        BookComment bookComment = new BookComment();
        bookComment.setBook(book);
        bookComment.setText(bookCommentText);
        BookComment savedComment = bookCommentRepository.save(bookComment);
        BookComment dbComment = entityManager.find(BookComment.class, savedComment.getId());
        checkBookCommentsEqual(savedComment, dbComment);
    }

    @DisplayName("Должен обновлять комментарий к книге")
    @Test
    void shouldUpdateBookComment() {
        long bookCommentId = 1L;
        String newText = "new text";
        BookComment bookComment = entityManager.find(BookComment.class, bookCommentId);
        bookComment.setText(newText);
        BookComment savedComment = bookCommentRepository.save(bookComment);
        BookComment dbComment = entityManager.find(BookComment.class, savedComment.getId());
        checkBookCommentsEqual(savedComment, dbComment);
    }

    @DisplayName("Должен удалять комментарий по id")
    @Test
    void shouldDeleteBookComment() {
        long bookCommentId = 1L;
        bookCommentRepository.deleteById(bookCommentId);
        BookComment bookComment = entityManager.find(BookComment.class, bookCommentId);
        assertNull(bookComment);
    }

    private void checkBookCommentsEqual(BookComment expectedBookComment, BookComment actualBookComment) {
        assertAll("Проверка полей комментариев",
                () -> assertEquals(expectedBookComment.getText(), actualBookComment.getText()),
                () -> assertEquals(expectedBookComment.getBook(), actualBookComment.getBook())
        );
    }
}
