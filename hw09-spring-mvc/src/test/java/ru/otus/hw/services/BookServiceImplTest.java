package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mappers.AuthorMapper;
import ru.otus.hw.mappers.GenreMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Transactional(propagation = Propagation.NEVER)
@DisplayName("Сервис для работы с книгами")
@SpringBootTest
class BookServiceImplTest {

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

    private final String bookId = "bookId1";
    private final String title = "title";

    private final Author author1 = new Author("1a", "Author_1");
    private final Genre genre1 = new Genre("1g", "Genre_1");

    @Test
    @DisplayName("Добавление: Должно пройти успешно")
    void insertBook_success() {
        List<String> genreIds = List.of(genre1.getId());
        List<Genre> genres = Collections.singletonList(genre1);

        BookCreateDto bookCreateDto = new BookCreateDto();
        bookCreateDto.setTitle(title);
        bookCreateDto.setAuthorId(author1.getId());
        bookCreateDto.setGenreIds(genreIds);

        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author1);
        book.setGenres(genres);

        BookDto expectedBookDto = new BookDto();
        expectedBookDto.setTitle(title);
        expectedBookDto.setAuthor(authorMapper.toDto(author1));
        expectedBookDto.setGenres(genreMapper.toDto(genres));

        Optional<Author> author = Optional.of(new Author());
        when(authorRepository.findById(anyString())).thenReturn(author);
        when(genreRepository.findAllByIdIn(anyList())).thenReturn(genres);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        assertEquals(expectedBookDto, bookService.insert(bookCreateDto));
    }

    @Test
    @DisplayName("Добавление: Должен выводить ошибку при пустом genreIds")
    void insertBook_genreIds_empty() {
        List<String> genreIds = Collections.emptyList();

        BookCreateDto bookCreateDto = new BookCreateDto();
        bookCreateDto.setTitle(title);
        bookCreateDto.setAuthorId(author1.getId());
        bookCreateDto.setGenreIds(genreIds);

        assertThrows(IllegalArgumentException.class,
                () -> bookService.insert(bookCreateDto));
    }

    @Test
    @DisplayName("Добавление: Должен выводить ошибку при отсутствии автора в БД")
    void insertBook_author_not_exist() {
        List<String> genreIds = List.of(genre1.getId());

        BookCreateDto bookCreateDto = new BookCreateDto();
        bookCreateDto.setTitle(title);
        bookCreateDto.setAuthorId(author1.getId());
        bookCreateDto.setGenreIds(genreIds);

        when(authorRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookService.insert(bookCreateDto));
    }

    @Test
    @DisplayName("Добавление: Должен выводить ошибку при отсутствии жанров в БД")
    void insertBook_genre_not_exist() {
        List<String> genreIds = List.of(genre1.getId());

        BookCreateDto bookCreateDto = new BookCreateDto();
        bookCreateDto.setTitle(title);
        bookCreateDto.setAuthorId(author1.getId());
        bookCreateDto.setGenreIds(genreIds);

        Optional<Author> author = Optional.of(new Author());
        when(authorRepository.findById(anyString())).thenReturn(author);
        when(genreRepository.findAllByIdIn(anyList())).thenReturn(Collections.emptyList());

        assertThrows(EntityNotFoundException.class,
                () -> bookService.insert(bookCreateDto));
    }

    @Test
    @DisplayName("Обновление: должно пройти успешно")
    void updateBook_success() {
        List<String> genreIds = List.of(genre1.getId());
        List<Genre> genres = Collections.singletonList(genre1);

        BookUpdateDto bookUpdateDto = new BookUpdateDto();
        bookUpdateDto.setId(bookId);
        bookUpdateDto.setTitle(title);
        bookUpdateDto.setAuthorId(author1.getId());
        bookUpdateDto.setGenreIds(genreIds);

        Book book = new Book();
        book.setId(bookId);
        book.setTitle(title);
        book.setAuthor(author1);
        book.setGenres(genres);

        BookDto expectedBookDto = new BookDto();
        expectedBookDto.setId(bookId);
        expectedBookDto.setTitle(title);
        expectedBookDto.setAuthor(authorMapper.toDto(author1));
        expectedBookDto.setGenres(genreMapper.toDto(genres));

        when(authorRepository.findById(anyString())).thenReturn(Optional.of(author1));
        when(genreRepository.findAllByIdIn(anyList())).thenReturn(genres);
        when(bookRepository.findById(anyString())).thenReturn(Optional.of(book));

        when(bookRepository.save(any(Book.class))).thenReturn(book);

        assertEquals(expectedBookDto, bookService.update(bookUpdateDto));
    }

    @Test
    @DisplayName("Обновление: Должен выводить ошибку при отсутствии книги")
    void updateBook_book_not_exist() {
        List<String> genreIds = List.of(genre1.getId());

        BookUpdateDto bookUpdateDto = new BookUpdateDto();
        bookUpdateDto.setId(bookId);
        bookUpdateDto.setTitle(title);
        bookUpdateDto.setAuthorId(author1.getId());
        bookUpdateDto.setGenreIds(genreIds);

        when(bookRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookService.update(bookUpdateDto));
    }

    @Test
    @DisplayName("Обновление: Должен выводить ошибку при отсутствии книги")
    void updateBook_genreIds_empty() {
        List<String> genreIds = Collections.emptyList();

        BookUpdateDto bookUpdateDto = new BookUpdateDto();
        bookUpdateDto.setId(bookId);
        bookUpdateDto.setTitle(title);
        bookUpdateDto.setAuthorId(author1.getId());
        bookUpdateDto.setGenreIds(genreIds);

        Optional<Book> book = Optional.of(new Book());
        when(bookRepository.findById(anyString())).thenReturn(book);

        assertThrows(IllegalArgumentException.class,
                () -> bookService.update(bookUpdateDto));
    }

    @Test
    @DisplayName("Обновление: Должен выводить ошибку при отсутствии автора в БД")
    void updateBook_author_not_exist() {
        List<String> genreIds = List.of(genre1.getId());

        BookUpdateDto bookUpdateDto = new BookUpdateDto();
        bookUpdateDto.setId(bookId);
        bookUpdateDto.setTitle(title);
        bookUpdateDto.setAuthorId(author1.getId());
        bookUpdateDto.setGenreIds(genreIds);

        when(authorRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookService.update(bookUpdateDto));
    }

    @Test
    @DisplayName("Обновление: Должен выводить ошибку при отсутствии автора в БД")
    void updateBook_genre_not_exist() {
        List<String> genreIds = List.of(genre1.getId());

        BookUpdateDto bookUpdateDto = new BookUpdateDto();
        bookUpdateDto.setId(bookId);
        bookUpdateDto.setTitle(title);
        bookUpdateDto.setAuthorId(author1.getId());
        bookUpdateDto.setGenreIds(genreIds);

        Optional<Author> author = Optional.of(new Author());
        when(authorRepository.findById(anyString())).thenReturn(author);
        when(genreRepository.findAllByIdIn(anyList())).thenReturn(Collections.emptyList());

        assertThrows(EntityNotFoundException.class,
                () -> bookService.update(bookUpdateDto));
    }

}