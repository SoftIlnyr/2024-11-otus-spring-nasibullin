package ru.otus.hw.rest;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BookRestController {

    private final BookService bookService;

    private final CommentService commentService;

    @GetMapping(path = {"/api/books", "/api/books/"})
    @RateLimiter(name = "bookRateLimiter")
    @CircuitBreaker(name = "bookCircuitBreaker")
    public ResponseEntity<List<BookDto>> findAllBooks() {
        return ResponseEntity.ok(bookService.findAll());
    }

    @PostMapping(path = {"/api/books", "/api/books/"})
    @RateLimiter(name = "bookRateLimiter")
    @CircuitBreaker(name = "bookCircuitBreaker")
    public ResponseEntity<BookDto> addNewBook(@Valid @RequestBody BookCreateDto bookCreateDto) {
        var savedBook = bookService.insert(bookCreateDto);

        return ResponseEntity.ok(savedBook);
    }

    @PutMapping("/api/books/{bookId}")
    @RateLimiter(name = "bookRateLimiter")
    @CircuitBreaker(name = "bookCircuitBreaker")
    public ResponseEntity<BookDto> updateBook(@PathVariable("bookId") String bookId,
                             @Valid @RequestBody BookUpdateDto bookUpdateDto) {
        var savedBook = bookService.update(bookUpdateDto);

        return ResponseEntity.ok(savedBook);
    }

    @DeleteMapping("/api/books/{bookId}")
    @RateLimiter(name = "bookRateLimiter")
    @CircuitBreaker(name = "bookCircuitBreaker")
    public ResponseEntity<String> deleteBook(@PathVariable("bookId") String bookId) {
        bookService.deleteById(bookId);

        return ResponseEntity.ok("Book with id " + bookId + " deleted.");
    }

    @PostMapping("/api/books/{bookId}/comments")
    @RateLimiter(name = "bookRateLimiter")
    @CircuitBreaker(name = "bookCircuitBreaker")
    public ResponseEntity<CommentDto> addComment(@PathVariable("bookId") String bookId,
                                 @RequestBody CommentCreateDto commentCreateDto) {
        var savedComment = commentService.addComment(commentCreateDto);

        return ResponseEntity.ok(savedComment);
    }
}
