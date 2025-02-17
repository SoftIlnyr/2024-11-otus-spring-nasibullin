package ru.otus.hw.services;

import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CommentUpdateDto;

import java.util.List;

public interface CommentService {

    List<CommentDto> findAllComments();

    List<CommentDto> findAllCommentsByBookId(String bookId);

    CommentDto addComment(CommentCreateDto commentCreateDto);

    CommentDto updateComment(CommentUpdateDto commentUpdateDto);

    void deleteCommentById(String bookCommentId);
}
