package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.services.CommentService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    @Override
    public List<Comment> findAllComments() {
        return commentRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Comment> findAllCommentsByBookId(long bookId) {
        return commentRepository.findByBookId(bookId);
    }

    @Transactional
    @Override
    public Comment addComment(long bookId, String comment) {
        Book book = bookRepository.findById(bookId).orElseThrow(() ->
                new EntityNotFoundException("Book with id %s not found".formatted(bookId)));
        Comment bookComment = new Comment(0, book, comment);
        return commentRepository.save(bookComment);
    }

    @Transactional
    @Override
    public Comment updateComment(long commentId, String commentText) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new EntityNotFoundException("Comment with id %s not found".formatted(commentId)));
        comment.setText(commentText);
        return commentRepository.save(comment);
    }

    @Transactional
    @Override
    public void deleteCommentById(long bookCommentId) {
        commentRepository.deleteById(bookCommentId);
    }
}
