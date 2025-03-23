package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
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

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    @PreAuthorize("hasRole(T(ru.otus.hw.security.UserRole).READER)")
    @Override
    public List<CommentDto> findAllComments() {
        return commentMapper.toDto(commentRepository.findAll());
    }

    @Override
    public List<CommentDto> findAllCommentsByBookId(String bookId) {
        return commentMapper.toDto(commentRepository.findByBookId(bookId));
    }

    @PreAuthorize("hasRole(T(ru.otus.hw.security.UserRole).READER)")
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
