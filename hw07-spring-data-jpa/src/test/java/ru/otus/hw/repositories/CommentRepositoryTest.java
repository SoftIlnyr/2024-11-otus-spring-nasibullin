package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Репозиторий на основе Jpa для работы с комментариями к книгам")
@DataJpaTest
public class CommentRepositoryTest {

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
        assertThat(actualComment).usingRecursiveComparison().isEqualTo(expectedComment);
    }

    @DisplayName("Должен найти комментарии к книге по id книги")
    @Test
    void shouldFindBookCommentsByBookId() {
        long bookCommentId = 1L;
        Book book = entityManager.find(Book.class, bookCommentId);
        List<Comment> actualComments = commentRepository.findByBookId(bookCommentId);
        assertEquals(3, actualComments.size());
    }

}
