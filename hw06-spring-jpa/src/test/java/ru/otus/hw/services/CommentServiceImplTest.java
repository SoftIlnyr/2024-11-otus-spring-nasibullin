package ru.otus.hw.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Comment;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional(propagation = Propagation.NEVER)
@DisplayName("Сервис для работы с комментариями")
@SpringBootTest
public class CommentServiceImplTest {

    @Autowired
    private CommentService commentService;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void findAllComments() {
        List<Comment> comments = commentService.findAllComments();
        assertEquals(6, comments.size());
    }

    @Test
    void findAllCommentsByBookId() {
        long bookId = 1L;
        List<Comment> comments = commentService.findAllCommentsByBookId(bookId);
        assertEquals(3, comments.size());
    }

    @Test
    void addComment() {
        long bookId = 1L;
        String text = "Text";

        Comment savedComment = commentService.addComment(bookId, text);

        Comment dbComment = entityManager.find(Comment.class, savedComment.getId());
        assertNotNull(dbComment);
        assertEquals(text, dbComment.getText());
    }

    @Test
    void updateComment() {
        long commentId = 1L;
        Comment comment = entityManager.find(Comment.class, commentId);
        String text = "Text";
        assertNotEquals(text, comment.getText());

        commentService.updateComment(commentId, text);

        Comment commentAfterUpdate = entityManager.find(Comment.class, commentId);
        assertEquals(text, commentAfterUpdate.getText());
    }

    @Test
    void deleteCommentById() {
        long commentId = 1L;
        Comment comment = entityManager.find(Comment.class, commentId);
        assertNotNull(comment);

        commentService.deleteCommentById(commentId);
        Comment commentAfterDelete = entityManager.find(Comment.class, commentId);

        assertNull(commentAfterDelete);
    }
}
