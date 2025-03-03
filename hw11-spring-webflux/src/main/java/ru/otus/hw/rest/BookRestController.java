package ru.otus.hw.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

@RequiredArgsConstructor
@RestController
public class BookRestController {

    private final BookService bookService;

    private final CommentService commentService;

    @GetMapping(path = {"/api/books", "/api/books/"})
    public Flux<BookDto> findAllBooks() {
        return bookService.findAll();
    }

    @PostMapping(path = {"/api/books", "/api/books/"})
    public Mono<BookDto> addNewBook(@Valid @RequestBody BookCreateDto bookCreateDto) {
        return bookService.insert(bookCreateDto);
    }

    @PutMapping("/api/books/{bookId}")
    public Mono<BookDto> updateBook(@PathVariable("bookId") String bookId,
                             @Valid @RequestBody BookUpdateDto bookUpdateDto) {
        return bookService.update(bookUpdateDto);
    }

    @DeleteMapping("/api/books/{bookId}")
    public Mono<Void> deleteBook(@PathVariable("bookId") String bookId) {
        return bookService.deleteById(bookId);
    }

    @PostMapping("/api/books/{bookId}/comments")
    public Mono<CommentDto> addComment(@PathVariable("bookId") String bookId,
                                 @RequestBody @Valid CommentCreateDto commentCreateDto) {
        return commentService.addComment(commentCreateDto);
    }
}
