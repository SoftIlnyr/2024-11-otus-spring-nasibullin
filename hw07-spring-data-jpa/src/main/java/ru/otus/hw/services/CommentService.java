package ru.otus.hw.services;

import ru.otus.hw.models.Comment;

import java.util.List;

public interface CommentService {

    List<Comment> findAllComments();

    List<Comment> findAllCommentsByBookId(long bookId);

    Comment addComment(long bookId, String comment);

    Comment updateComment(long bookCommentId, String comment);

    void deleteCommentById(long bookCommentId);
}
