package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Transactional(propagation = Propagation.NEVER)
@DisplayName("Сервис для работы с книгами")
@SpringBootTest
class BookServiceImplTest {

    @Autowired
    private BookService bookService;

    @MockitoBean
    private BookRepository bookRepository;

    @MockitoBean
    private AuthorRepository authorRepository;

    @MockitoBean
    private GenreRepository genreRepository;

    @Autowired
    private MongoOperations mongoOperations;

    @Test
    @DisplayName("Добавление: должно пройти успешно")
    void insertBook_success() {
        String title = "title1";
        String authorId = "authorId1";
        String genreId = "genre1";
        Set<String> genresId = Set.of(genreId);
        List<Genre> genres = Collections.singletonList(new Genre(genreId));

        Book book = new Book();

        Optional<Author> author = Optional.of(new Author());
        when(authorRepository.findById(anyString())).thenReturn(author);
        when(genreRepository.findAllByIdIn(anySet())).thenReturn(genres);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        assertEquals(book, bookService.insert(title, authorId, genresId));
    }

    @Test
    @DisplayName("Добавление: Должен выводить ошибку при пустом genreIds")
    void insertBook_genreIds_empty() {
        String title = "title1";
        String authorId = "authorId1";
        Set<String> genresId = Collections.emptySet();

        assertThrows(IllegalArgumentException.class,
                () -> bookService.insert(title, authorId, genresId));
    }

    @Test
    @DisplayName("Добавление: Должен выводить ошибку при отсутствии автора в БД")
    void insertBook_author_not_exist() {
        String title = "title1";
        String authorId = "authorId1";
        Set<String> genresId = Set.of("genre1");

        when(authorRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookService.insert(title, authorId, genresId));
    }

    @Test
    @DisplayName("Добавление: Должен выводить ошибку при отсутствии жанров в БД")
    void insertBook_genre_not_exist() {
        String title = "title1";
        String authorId = "authorId1";
        Set<String> genresId = Set.of("genre1");

        Optional<Author> author = Optional.of(new Author());
        when(authorRepository.findById(anyString())).thenReturn(author);
        when(genreRepository.findAllByIdIn(anySet())).thenReturn(Collections.emptyList());

        assertThrows(EntityNotFoundException.class,
                () -> bookService.insert(title, authorId, genresId));
    }

    @Test
    @DisplayName("Обновление: должно пройти успешно")
    void updateBook_success() {
        String bookId = "bookId1";
        String title = "title1";
        String authorId = "authorId1";
        String genreId = "genre1";
        Set<String> genresId = Set.of(genreId);
        List<Genre> genres = Collections.singletonList(new Genre(genreId));

        Book book = new Book();

        Optional<Author> author = Optional.of(new Author());
        when(authorRepository.findById(anyString())).thenReturn(author);
        when(genreRepository.findAllByIdIn(anySet())).thenReturn(genres);
        when(bookRepository.findById(anyString())).thenReturn(Optional.of(book));

        when(bookRepository.save(any(Book.class))).thenReturn(book);

        assertEquals(book, bookService.update(bookId, title, authorId, genresId));
    }

    @Test
    @DisplayName("Обновление: Должен выводить ошибку при отсутствии книги")
    void updateBook_book_not_exist() {
        String bookId = "bookId1";
        String title = "title1";
        String authorId = "authorId1";
        Set<String> genresId = Set.of("genre1");

        when(bookRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookService.update(bookId, title, authorId, genresId));
    }

    @Test
    @DisplayName("Обновление: Должен выводить ошибку при отсутствии книги")
    void updateBook_genreIds_empty() {
        String bookId = "bookId1";
        String title = "title1";
        String authorId = "authorId1";
        Set<String> genresId = Collections.emptySet();

        Optional<Book> book = Optional.of(new Book());
        when(bookRepository.findById(anyString())).thenReturn(book);

        assertThrows(IllegalArgumentException.class,
                () -> bookService.update(bookId, title, authorId, genresId));
    }

    @Test
    @DisplayName("Обновление: Должен выводить ошибку при отсутствии автора в БД")
    void updateBook_author_not_exist() {
        String bookId = "bookId1";
        String title = "title1";
        String authorId = "authorId1";
        Set<String> genresId = Set.of("genre1");

        when(authorRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookService.update(bookId, title, authorId, genresId));
    }

    @Test
    @DisplayName("Обновление: Должен выводить ошибку при отсутствии автора в БД")
    void updateBook_genre_not_exist() {
        String bookId = "bookId1";
        String title = "title1";
        String authorId = "authorId1";
        Set<String> genresId = Set.of("genre1");

        Optional<Author> author = Optional.of(new Author());
        when(authorRepository.findById(anyString())).thenReturn(author);
        when(genreRepository.findAllByIdIn(anySet())).thenReturn(Collections.emptyList());

        assertThrows(EntityNotFoundException.class,
                () -> bookService.update(bookId, title, authorId, genresId));
    }

}