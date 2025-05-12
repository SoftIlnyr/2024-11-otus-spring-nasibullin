package ru.otus.hw.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CommentUpdateDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mappers.CommentMapper;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.Collections;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    @PreAuthorize("hasRole(T(ru.otus.hw.security.UserRole).READER)")
    @CircuitBreaker(name = "defaultCircuitBreaker", fallbackMethod = "findAllCommentsFallback")
    @Override
    public List<CommentDto> findAllComments() {
        return commentMapper.toDto(commentRepository.findAll());
    }

    private List<CommentDto> findAllCommentsFallback(Exception exception) {
        log.error("CircuitBreaker triggered due to: " + exception.getMessage());
        return Collections.emptyList();
    }

    @PreAuthorize("hasRole(T(ru.otus.hw.security.UserRole).READER)")
    @CircuitBreaker(name = "defaultCircuitBreaker", fallbackMethod = "findAllCommentsByBookIdFallback")
    @Override
    public List<CommentDto> findAllCommentsByBookId(String bookId) {
        return commentMapper.toDto(commentRepository.findByBookId(bookId));
    }

    private List<CommentDto> findAllCommentsByBookIdFallback(Exception exception) {
        log.error("CircuitBreaker triggered due to: " + exception.getMessage());
        return Collections.emptyList();
    }

    @PreAuthorize("hasRole(T(ru.otus.hw.security.UserRole).READER)")
    @RateLimiter(name = "bookRateLimiter")
    @CircuitBreaker(name = "defaultCircuitBreaker")
    @Override
    public CommentDto addComment(CommentCreateDto commentCreateDto) {
        if (commentCreateDto == null) {
            throw new IllegalArgumentException("commentCreateDto cannot be null");
        }

        String bookId = commentCreateDto.getBookId();
        String text = commentCreateDto.getText();

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(bookId)));
        Comment bookComment = new Comment(book, text);
        Comment savedComment = commentRepository.save(bookComment);
        return commentMapper.toDto(savedComment);
    }

    @PreAuthorize("hasRole(T(ru.otus.hw.security.UserRole).READER)")
    @Override
    public CommentDto updateComment(CommentUpdateDto commentUpdateDto) {
        if (commentUpdateDto == null) {
            throw new IllegalArgumentException("commentUpdateDto cannot be null");
        }

        String commentId = commentUpdateDto.getCommentId();
        String commentText = commentUpdateDto.getText();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id %s not found".formatted(commentId)));
        comment.setText(commentText);
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    @PreAuthorize("hasRole(T(ru.otus.hw.security.UserRole).READER)")
    @Override
    public void deleteCommentById(String bookCommentId) {
        commentRepository.deleteById(bookCommentId);
    }
}
