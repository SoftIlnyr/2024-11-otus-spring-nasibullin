package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Репозиторий на основе Jpa для работы с комментариями к книгам")
@DataJpaTest
@Import(JpaCommentRepository.class)
public class JpaCommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @DisplayName("Должен найти все 6 комментов из data.sql")
    @Test
    void shouldFindAllBookComments() {
        assertEquals(6, commentRepository.findAll().size());
    }

    @DisplayName("Должен найти комментарий по его id")
    @Test
    void shouldFindBookCommentById() {
        long bookCommentId = 1L;
        Comment expectedComment = entityManager.find(Comment.class, bookCommentId);
        Optional<Comment> bookComment = commentRepository.findById(bookCommentId);
        assertTrue(bookComment.isPresent());
        Comment actualComment = bookComment.get();
        checkBookCommentsEqual(expectedComment, actualComment);
    }

    @DisplayName("Должен найти комментарии к книге по id книги")
    @Test
    void shouldFindBookCommentsByBookId() {
        long bookCommentId = 1L;
        Book book = entityManager.find(Book.class, bookCommentId);
        List<Comment> actualComments = commentRepository.findByBookId(bookCommentId);
        assertEquals(3, actualComments.size());
    }

    @DisplayName("Должен добавлять комментарий к книге")
    @Test
    void shouldInsertBookComment() {
        long bookId = 1L;
        String bookCommentText = "test text";
        Book book = entityManager.find(Book.class, bookId);
        Comment comment = new Comment();
        comment.setBook(book);
        comment.setText(bookCommentText);
        Comment savedComment = commentRepository.save(comment);
        Comment dbComment = entityManager.find(Comment.class, savedComment.getId());
        checkBookCommentsEqual(savedComment, dbComment);
    }

    @DisplayName("Должен обновлять комментарий к книге")
    @Test
    void shouldUpdateBookComment() {
        long bookCommentId = 1L;
        String newText = "new text";
        Comment comment = entityManager.find(Comment.class, bookCommentId);
        comment.setText(newText);
        Comment savedComment = commentRepository.save(comment);
        Comment dbComment = entityManager.find(Comment.class, savedComment.getId());
        checkBookCommentsEqual(savedComment, dbComment);
    }

    @DisplayName("Должен удалять комментарий по id")
    @Test
    void shouldDeleteBookComment() {
        long bookCommentId = 1L;
        commentRepository.deleteById(bookCommentId);
        Comment comment = entityManager.find(Comment.class, bookCommentId);
        assertNull(comment);
    }

    private void checkBookCommentsEqual(Comment expectedComment, Comment actualComment) {
        assertAll("Проверка полей комментариев",
                () -> assertEquals(expectedComment.getText(), actualComment.getText()),
                () -> assertEquals(expectedComment.getBook(), actualComment.getBook())
        );
    }
}
