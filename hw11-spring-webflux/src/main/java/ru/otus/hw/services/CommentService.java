package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CommentUpdateDto;

public interface CommentService {

    Flux<CommentDto> findAllComments();

    Flux<CommentDto> findAllCommentsByBookId(String bookId);

    Mono<CommentDto> addComment(CommentCreateDto commentCreateDto);

    Mono<CommentDto> updateComment(CommentUpdateDto commentUpdateDto);

    void deleteCommentById(String bookCommentId);
}
