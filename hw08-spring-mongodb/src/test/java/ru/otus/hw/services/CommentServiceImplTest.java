package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    @DisplayName("Добавление: Должен выводить ошибку при отсутствии книги")
    void insertComment_book_not_exist() {
        String bookId = "bookId";
        String comment = "comment";

        when(bookRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> commentService.addComment(bookId, comment));
    }

    @Test
    @DisplayName("Добавление: Должен выводить ошибку при отсутствии комментерия")
    void updateComment_comment_not_exist() {
        String commentId = "commentId";
        String comment = "comment";

        when(commentRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> commentService.updateComment(commentId, comment));
    }
}