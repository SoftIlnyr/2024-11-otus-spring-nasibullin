package ru.otus.hw.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.BookUpdateDto;
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

    @GetMapping(path = {"/books", "/books/"})
    public Mono<String> getBooks(Model model) {
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("book_form", new BookCreateDto());

        return Mono.just("books");
    }

    @GetMapping("/books/{bookId}")
    public Mono<String> getBookInfo(@PathVariable(name = "bookId") String bookId, Model model) {
        var book = bookService.findById(bookId);

        var comments = commentService.findAllCommentsByBookId(bookId);

        model.addAttribute("book", book);
        model.addAttribute("comments", comments);
        model.addAttribute("comment_form", new CommentCreateDto());

        return Mono.just("books_detail");
    }

    @GetMapping("books/{bookId}/actions")
    public Mono<String> getBookActions(@PathVariable("bookId") String bookId, Model model) {
        var bookMono = bookService.findById(bookId);

        Mono<BookUpdateDto> bookUpdateDtoMono = bookMono.map(book -> {
                    BookUpdateDto bookUpdateDto = new BookUpdateDto();
                    bookUpdateDto.setId(book.getId());
                    bookUpdateDto.setTitle(book.getTitle());
                    bookUpdateDto.setAuthorId(book.getAuthor().getId());
                    bookUpdateDto.setGenreIds(book.getGenres().stream().map(genre -> genre.getId()).toList());
                    return bookUpdateDto;
                }
        );

        model.addAttribute("book_form", bookUpdateDtoMono);
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());

        return Mono.just("books_actions");
    }
}
