package ru.otus.hw.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mappers.BookMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.Collections;
import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

@Log4j2
@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    @PreAuthorize("hasRole(T(ru.otus.hw.security.UserRole).READER)")
    @Override
    public BookDto findById(String id) {
        Book book = getBook(id);
        return bookMapper.toDto(book);
    }

    @PreAuthorize("hasRole(T(ru.otus.hw.security.UserRole).READER)")
    @RateLimiter(name = "bookRateLimiter")
    @CircuitBreaker(name = "defaultCircuitBreaker", fallbackMethod = "findAllFallback")
    @Override
    public List<BookDto> findAll() {
        return bookMapper.toDto(bookRepository.findAll());
    }

    private List<CommentDto> findAllFallback(Exception exception) {
        log.error("CircuitBreaker triggered due to: " + exception.getMessage());
        return Collections.emptyList();
    }

    @PreAuthorize("hasRole(T(ru.otus.hw.security.UserRole).AUTHOR)")
    @RateLimiter(name = "bookRateLimiter")
    @CircuitBreaker(name = "defaultCircuitBreaker")
    @Override
    public BookDto insert(BookCreateDto bookCreateDto) {
        String title = bookCreateDto.getTitle();
        List<String> genreIds = bookCreateDto.getGenreIds();

        var author = getAuthor(bookCreateDto.getAuthorId());
        var genres = getGenres(genreIds);

        var book = new Book(title, author, genres);
        Book savedBook = bookRepository.save(book);
        return bookMapper.toDto(savedBook);
    }

    @PreAuthorize("hasRole(T(ru.otus.hw.security.UserRole).AUTHOR)")
    @RateLimiter(name = "bookRateLimiter")
    @CircuitBreaker(name = "defaultCircuitBreaker")
    @Override
    public BookDto update(BookUpdateDto bookUpdateDto) {
        Book book = getBook(bookUpdateDto.getId());

        String authorId = bookUpdateDto.getAuthorId();
        var author = getAuthor(authorId);
        var genres = getGenres(bookUpdateDto.getGenreIds());

        book.setTitle(bookUpdateDto.getTitle());
        book.setAuthor(author);
        book.setGenres(genres);

        Book savedBook = bookRepository.save(book);
        return bookMapper.toDto(savedBook);
    }

    private Book getBook(String bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found"
                        .formatted(bookId)));
    }

    private Author getAuthor(String authorId) {
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %s not found"
                        .formatted(authorId)));
    }

    private List<Genre> getGenres(List<String> genreIds) {
        var genres = genreRepository.findAllByIdIn(genreIds);
        if (isEmpty(genres) || genreIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genreIds));
        }
        return genres;
    }


    @PreAuthorize("hasRole(T(ru.otus.hw.security.UserRole).AUTHOR)")
    @RateLimiter(name = "bookRateLimiter")
    @CircuitBreaker(name = "defaultCircuitBreaker")
    @Override
    public void deleteById(String id) {
        bookRepository.deleteById(id);
    }

}
