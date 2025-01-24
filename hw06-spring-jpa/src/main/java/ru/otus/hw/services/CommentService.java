package ru.otus.hw.services;

import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Comment;

import java.util.List;

public interface CommentService {

    List<Comment> findAllComments();

    @Transactional(readOnly = true)
    List<Comment> findAllCommentsByBookId(long bookId);

    @Transactional
    Comment addComment(long bookId, String comment);

    @Transactional
    Comment updateComment(long bookCommentId, String comment);

    @Transactional
    void deleteCommentById(long bookCommentId);
}
