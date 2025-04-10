package ru.otus.hw.services.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.mappers.AuthorMapper;
import ru.otus.hw.mappers.GenreMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.services.BookService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static ru.otus.hw.mongock.testchangelog.TestValues.AUTHOR_1;
import static ru.otus.hw.mongock.testchangelog.TestValues.BOOK_1;
import static ru.otus.hw.mongock.testchangelog.TestValues.GENRE_1;

@Transactional(propagation = Propagation.NEVER)
@DisplayName("Сервис для работы с книгами")
@SpringBootTest
public class BookServiceImplSecurityTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorMapper authorMapper;

    @Autowired
    private GenreMapper genreMapper;

    @MockitoBean
    private BookRepository bookRepository;

    @MockitoBean
    private AuthorRepository authorRepository;

    @MockitoBean
    private GenreRepository genreRepository;

    private final String bookId = BOOK_1.getId();
    private final String title = BOOK_1.getTitle();

    private final Author author1 = new Author(AUTHOR_1.getId(), AUTHOR_1.getFullName());
    private final Genre genre1 = new Genre(GENRE_1.getId(), GENRE_1.getName());

    private static final String ROLE_AUTHOR = "AUTHOR";
    private static final String ROLE_READER = "READER";

    @Test
    @DisplayName("Добавление, Автор: Должно пройти успешно")
    @WithMockUser(roles = ROLE_AUTHOR)
    void insertBook_author() {
        List<String> genreIds = List.of(genre1.getId());
        List<Genre> genres = Collections.singletonList(genre1);

        BookCreateDto bookCreateDto = new BookCreateDto();
        bookCreateDto.setTitle(title);
        bookCreateDto.setAuthorId(author1.getId());
        bookCreateDto.setGenreIds(genreIds);

        Optional<Author> author = Optional.of(new Author());
        when(authorRepository.findById(anyString())).thenReturn(author);
        when(genreRepository.findAllByIdIn(anyList())).thenReturn(genres);

        bookService.insert(bookCreateDto);
    }

    @Test
    @DisplayName("Добавление, читатель: доступ запрещен")
    @WithMockUser(roles = ROLE_READER)
    void insertBook_reader() {
        List<String> genreIds = List.of(genre1.getId());
        List<Genre> genres = Collections.singletonList(genre1);

        BookCreateDto bookCreateDto = new BookCreateDto();
        bookCreateDto.setTitle(title);
        bookCreateDto.setAuthorId(author1.getId());
        bookCreateDto.setGenreIds(genreIds);

        Optional<Author> author = Optional.of(new Author());
        when(authorRepository.findById(anyString())).thenReturn(author);
        when(genreRepository.findAllByIdIn(anyList())).thenReturn(genres);

        assertThrows(AccessDeniedException.class, () -> bookService.insert(bookCreateDto));
    }

    @Test
    @DisplayName("Обновление, автор: должно пройти успешно")
    @WithMockUser(roles = ROLE_AUTHOR)
    void updateBook_author() {
        List<String> genreIds = List.of(genre1.getId());
        List<Genre> genres = Collections.singletonList(genre1);

        BookUpdateDto bookUpdateDto = new BookUpdateDto();
        bookUpdateDto.setId(bookId);
        bookUpdateDto.setTitle(title);
        bookUpdateDto.setAuthorId(author1.getId());
        bookUpdateDto.setGenreIds(genreIds);

        Book book = new Book();

        when(authorRepository.findById(anyString())).thenReturn(Optional.of(author1));
        when(genreRepository.findAllByIdIn(anyList())).thenReturn(genres);
        when(bookRepository.findById(anyString())).thenReturn(Optional.of(book));

        bookService.update(bookUpdateDto);
    }

    @Test
    @DisplayName("Обновление, читатель: доступ запрещен")
    @WithMockUser(roles = ROLE_READER)
    void updateBook_reader() {
        List<String> genreIds = List.of(genre1.getId());
        List<Genre> genres = Collections.singletonList(genre1);

        BookUpdateDto bookUpdateDto = new BookUpdateDto();
        bookUpdateDto.setId(bookId);
        bookUpdateDto.setTitle(title);
        bookUpdateDto.setAuthorId(author1.getId());
        bookUpdateDto.setGenreIds(genreIds);

        Book book = new Book();

        when(authorRepository.findById(anyString())).thenReturn(Optional.of(author1));
        when(genreRepository.findAllByIdIn(anyList())).thenReturn(genres);
        when(bookRepository.findById(anyString())).thenReturn(Optional.of(book));

        assertThrows(AccessDeniedException.class, () -> bookService.update(bookUpdateDto));
    }

}
