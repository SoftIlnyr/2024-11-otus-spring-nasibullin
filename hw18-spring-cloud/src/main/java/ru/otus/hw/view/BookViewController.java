package ru.otus.hw.view;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.security.RoleCheckService;
import ru.otus.hw.security.UserRole;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

@RequiredArgsConstructor
@Controller
public class BookViewController {

    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    private final CommentService commentService;

    private final RoleCheckService roleCheckService;

    @GetMapping(path = {"/books", "/books/"})
    @RateLimiter(name = "bookRateLimiter")
    public ModelAndView getBooks() {
        ModelAndView modelAndView = new ModelAndView("books");

        modelAndView.addObject("authors", authorService.findAll());
        modelAndView.addObject("genres", genreService.findAll());
        modelAndView.addObject("book_form", new BookCreateDto());

        modelAndView.addObject("has_book_create_access", roleCheckService.hasAccess(UserRole.AUTHOR));

        return modelAndView;
    }

    @GetMapping("/books/{bookId}")
    @RateLimiter(name = "bookRateLimiter")
    @CircuitBreaker(name = "defaultCircuitBreaker")
    public ModelAndView getBookInfo(@PathVariable(name = "bookId") String bookId) {
        ModelAndView modelAndView = new ModelAndView("books_detail");

        var book = bookService.findById(bookId);

        var comments = commentService.findAllCommentsByBookId(bookId);

        modelAndView.addObject("book", book);
        modelAndView.addObject("comments", comments);
        modelAndView.addObject("comment_form", new CommentCreateDto());

        return modelAndView;
    }

    @GetMapping("books/{bookId}/actions")
    @RateLimiter(name = "bookRateLimiter")
    @CircuitBreaker(name = "defaultCircuitBreaker")
    public ModelAndView getBookActions(@PathVariable("bookId") String bookId) {
        ModelAndView modelAndView = new ModelAndView("books_actions");

        var book = bookService.findById(bookId);

        BookUpdateDto bookUpdateDto = new BookUpdateDto();
        bookUpdateDto.setId(book.getId());
        bookUpdateDto.setTitle(book.getTitle());
        bookUpdateDto.setAuthorId(book.getAuthor().getId());
        bookUpdateDto.setGenreIds(book.getGenres().stream().map(genre -> genre.getId()).toList());
        modelAndView.addObject("book_form", bookUpdateDto);

        modelAndView.addObject("authors", authorService.findAll());
        modelAndView.addObject("genres", genreService.findAll());

        return modelAndView;
    }
}
