package ru.otus.hw.services.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
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
import ru.otus.hw.services.CommentService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static ru.otus.hw.mongock.testchangelog.TestValues.BOOK_1;
import static ru.otus.hw.mongock.testchangelog.TestValues.COMMENT_1_1;

@Transactional(propagation = Propagation.NEVER)
@DisplayName("Сервис для работы с комментариями")
@SpringBootTest
class CommentServiceImplSecurityTest {

    @Autowired
    private CommentService commentService;

    @MockitoBean
    private BookRepository bookRepository;

    @MockitoBean
    private CommentRepository commentRepository;

    private final String bookId = BOOK_1.getId();
    private final String commentId = COMMENT_1_1.getId();
    private final String commentText = COMMENT_1_1.getText();

    private static final String ROLE_READER = "READER";

    @Test
    @DisplayName("Добавление, читатель: успешное добавление")
    @WithMockUser(roles = ROLE_READER)
    void insertComment_reader() {
        Book book = new Book();

        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setBookId(bookId);
        commentCreateDto.setText(commentText);

        when(bookRepository.findById(anyString())).thenReturn(Optional.of(book));

        commentService.addComment(commentCreateDto);
    }

    @Test
    @DisplayName("Добавление, аноним: доступ запрещен")
    @WithAnonymousUser
    void insertComment_anonymous() {
        Book book = new Book();

        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setBookId(bookId);
        commentCreateDto.setText(commentText);

        when(bookRepository.findById(anyString())).thenReturn(Optional.of(book));

        assertThrows(AccessDeniedException.class, () -> commentService.addComment(commentCreateDto));
    }

    @Test
    @DisplayName("Обновление, читатель: успешное обновление")
    @WithMockUser(roles = ROLE_READER)
    void updateComment_reader() {
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto();
        commentUpdateDto.setCommentId(commentId);
        commentUpdateDto.setText(commentText);

        Comment comment = new Comment();

        when(commentRepository.findById(anyString())).thenReturn(Optional.of(comment));

        commentService.updateComment(commentUpdateDto);
    }

    @Test
    @DisplayName("Обновление, аноним: доступ запрещен")
    @WithAnonymousUser
    void updateComment_anonymous() {
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto();
        commentUpdateDto.setCommentId(commentId);
        commentUpdateDto.setText(commentText);

        Comment comment = new Comment();

        when(commentRepository.findById(anyString())).thenReturn(Optional.of(comment));

        assertThrows(AccessDeniedException.class, () -> commentService.updateComment(commentUpdateDto));
    }

}