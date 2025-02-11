package ru.otus.hw.services;

import ru.otus.hw.models.Comment;

import java.util.List;

public interface CommentService {

    List<Comment> findAllComments();

    List<Comment> findAllCommentsByBookId(String bookId);

    Comment addComment(String bookId, String comment);

    Comment updateComment(String bookCommentId, String comment);

    void deleteCommentById(String bookCommentId);
}
