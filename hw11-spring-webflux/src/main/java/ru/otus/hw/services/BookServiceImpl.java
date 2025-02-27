package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mappers.BookMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    @Override
    public Mono<BookDto> findById(String id) {
        return getBook(id)
                .map(bookMapper::toDto);
    }

    @Override
    public Flux<BookDto> findAll() {
        return bookRepository.findAll().map(bookMapper::toDto);
    }

    @Override
    public Mono<BookDto> insert(BookCreateDto bookCreateDto) {
        String title = bookCreateDto.getTitle();
        List<String> genreIds = bookCreateDto.getGenreIds();

        Mono<Author> authorMono = getAuthor(bookCreateDto.getAuthorId());
        Mono<List<Genre>> genresMono = getGenres(genreIds).collectList();

        Mono<Book> savedBookMono = Mono.zip(authorMono, genresMono)
                .flatMap(objects -> {
                    Author author = objects.getT1();
                    List<Genre> genres = objects.getT2();

                    Book book = new Book(title, author, genres);
                    return bookRepository.save(book);
                });

        return savedBookMono.map(bookMapper::toDto);
    }

    @Override
    public Mono<BookDto> update(BookUpdateDto bookUpdateDto) {
        Mono<Book> bookMono = getBook(bookUpdateDto.getId());

        String authorId = bookUpdateDto.getAuthorId();
        Mono<Author> authorMono = getAuthor(authorId);
        Mono<List<Genre>> genresMono = getGenres(bookUpdateDto.getGenreIds()).collectList();

        Mono<Book> savedBookMono = Mono.zip(bookMono, authorMono, genresMono)
                .flatMap(objects -> {
                    Book book = objects.getT1();
                    Author author = objects.getT2();
                    List<Genre> genres = objects.getT3();

                    book.setTitle(bookUpdateDto.getTitle());
                    book.setAuthor(author);
                    book.setGenres(genres);

                    return bookRepository.save(book);
                });

        return savedBookMono.map(bookMapper::toDto);
    }

    private Mono<Book> getBook(String bookId) {
        return bookRepository.findById(bookId)
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException("Book with id %s not found".formatted(bookId))));
    }

    private Mono<Author> getAuthor(String authorId) {
        return authorRepository.findById(authorId)
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException("Author with id %s not found".formatted(authorId))));
    }

    private Flux<Genre> getGenres(List<String> genreIds) {
        return genreRepository.findAllByIdIn(genreIds)
                .switchIfEmpty(Flux.error(
                        new EntityNotFoundException("Genres not found")));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return bookRepository.deleteById(id);
    }

}
