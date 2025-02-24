package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CommentUpdateDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mappers.CommentMapper;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    @Override
    public Flux<CommentDto> findAllComments() {
        return commentRepository.findAll().map(commentMapper::toDto);
    }

    @Override
    public Flux<CommentDto> findAllCommentsByBookId(String bookId) {
        return commentRepository.findByBookId(bookId).map(commentMapper::toDto);
    }

    @Override
    public Mono<CommentDto> addComment(CommentCreateDto commentCreateDto) {
        if (commentCreateDto == null) {
            return Mono.error(new IllegalArgumentException("commentCreateDto cannot be null"));
        }

        String bookId = commentCreateDto.getBookId();
        String text = commentCreateDto.getText();

        Mono<Comment> savedCommentMono = bookRepository.findById(bookId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Book with id %s not found".formatted(bookId))))
                .flatMap(book -> {
                    Comment bookComment = new Comment(book, text);
                    return commentRepository.save(bookComment);
                });

        return savedCommentMono.map(commentMapper::toDto);
    }

    @Override
    public Mono<CommentDto> updateComment(CommentUpdateDto commentUpdateDto) {
        if (commentUpdateDto == null) {
            return Mono.error(new IllegalArgumentException("commentCreateDto cannot be null"));
        }

        String commentId = commentUpdateDto.getCommentId();
        String commentText = commentUpdateDto.getText();

        Mono<Comment> savedCommentMono = commentRepository.findById(commentId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Comment with id %s not found"
                        .formatted(commentId))))
                .flatMap(comment -> {
                    comment.setText(commentText);
                    return commentRepository.save(comment);
                });
        return savedCommentMono.map(commentMapper::toDto);
    }

    @Override
    public void deleteCommentById(String bookCommentId) {
        commentRepository.deleteById(bookCommentId);
    }
}
