package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CommentUpdateDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Transactional(propagation = Propagation.NEVER)
@DisplayName("Сервис для работы с комментариями")
@SpringBootTest
class CommentServiceImplTest {

    @Autowired
    private CommentService commentService;

    @MockitoBean
    private BookRepository bookRepository;

    @MockitoBean
    private CommentRepository commentRepository;

    private final String bookId = "bookId";
    private final String commentId = "commentId";
    private final String commentText = "text";

    @Test
    @DisplayName("Добавление: успешное добавление")
    void insertComment_success() {
        Book book = new Book();

        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setBookId(bookId);
        commentCreateDto.setText(commentText);

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setText(commentText);

        CommentDto expectedComment = new CommentDto();
        expectedComment.setId(commentId);
        expectedComment.setText(commentText);

        when(bookRepository.findById(anyString())).thenReturn(Optional.of(book));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        assertEquals(expectedComment, commentService.addComment(commentCreateDto));
    }

    @Test
    @DisplayName("Добавление: Должен выводить ошибку при отсутствии книги")
    void insertComment_book_not_exist() {
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setBookId(bookId);
        commentCreateDto.setText(commentText);

        when(bookRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> commentService.addComment(commentCreateDto));
    }

    @Test
    @DisplayName("Обновление: успешное обновление")
    void updateComment_success() {
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto();
        commentUpdateDto.setCommentId(commentId);
        commentUpdateDto.setText(commentText);

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setText(commentText);

        CommentDto expectedComment = new CommentDto();
        expectedComment.setId(commentId);
        expectedComment.setText(commentText);

        when(commentRepository.findById(anyString())).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        assertEquals(expectedComment, commentService.updateComment(commentUpdateDto));
    }

    @Test
    @DisplayName("Обновление: Должен выводить ошибку при отсутствии комментерия")
    void updateComment_comment_not_exist() {
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto();
        commentUpdateDto.setCommentId(commentId);
        commentUpdateDto.setText(commentText);

        when(commentRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> commentService.updateComment(commentUpdateDto));
    }
}