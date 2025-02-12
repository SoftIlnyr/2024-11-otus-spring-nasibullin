package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

@RequiredArgsConstructor
@Controller
public class BookController {

    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    private final CommentService commentService;

    @GetMapping(path = {"/books", "/books/"})
    public ModelAndView findAllBooks() {
        ModelAndView modelAndView = new ModelAndView("books");

        modelAndView.addObject("authors", authorService.findAll());
        modelAndView.addObject("genres", genreService.findAll());
        modelAndView.addObject("book_form", new BookCreateDto());

        return modelAndView;
    }

    @PostMapping("/books")
    public String addNewBook(@ModelAttribute("book_form") BookCreateDto bookCreateDto) {
        var savedBook = bookService.insert(bookCreateDto);

        return "redirect:/books/" + savedBook.getId();
    }

    @GetMapping("/books/{bookId}")
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

    @PutMapping("books/{bookId}")
    public String updateBook(@PathVariable("bookId") String bookId,
                             @ModelAttribute("book_form") BookUpdateDto bookUpdateDto) {
        var savedBook = bookService.update(bookUpdateDto);

        return String.format("redirect:/books/%s", savedBook.getId());
    }

    @DeleteMapping("/books/{bookId}")
    public String deleteBook(@PathVariable("bookId") String bookId) {
        bookService.deleteById(bookId);

        return "redirect:/books";
    }

    @PostMapping("/books/{bookId}/comments")
    public String addComment(@PathVariable("bookId") String bookId,
                             @ModelAttribute("comment_form") CommentCreateDto commentCreateDto) {
        var savedComment = commentService.addComment(commentCreateDto);

        return String.format("redirect:/books/%s", bookId);
    }
}
