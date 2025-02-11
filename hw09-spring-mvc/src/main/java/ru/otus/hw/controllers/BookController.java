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
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookSaveModel;
import ru.otus.hw.models.CommentSaveModel;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.HashSet;

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

        modelAndView.addObject("books", bookService.findAll());
        modelAndView.addObject("authors", authorService.findAll());
        modelAndView.addObject("genres", genreService.findAll());
        modelAndView.addObject("book_save", new BookSaveModel());

        return modelAndView;
    }

    @PostMapping("/books")
    public String addNewBook(@ModelAttribute("book_save") BookSaveModel book) {
        var savedBook = bookService.insert(book.getTitle(), book.getAuthorId(), new HashSet<>(book.getGenreIds()));

        return "redirect:/books/" + savedBook.getId();
    }

    @GetMapping("/books/{bookId}")
    public ModelAndView getBookInfo(@PathVariable(name = "bookId") String bookId) {
        ModelAndView modelAndView = new ModelAndView("books_detail");

        var book = bookService.findById(bookId).orElseThrow(
                () -> new EntityNotFoundException("Book with id %s not found".formatted(bookId))
        );

        var comments = commentService.findAllCommentsByBookId(bookId);

        modelAndView.addObject("book", book);
        modelAndView.addObject("comments", comments);
        modelAndView.addObject("commentSaveModel", new CommentSaveModel());

        return modelAndView;
    }

    @GetMapping("books/{bookId}/actions")
    public ModelAndView getBookActions(@PathVariable("bookId") String bookId) {
        ModelAndView modelAndView = new ModelAndView("books_actions");

        var book = bookService.findById(bookId).orElseThrow(
                () -> new EntityNotFoundException("Book with id %s not found".formatted(bookId))
        );

        BookSaveModel bookSaveModel = new BookSaveModel();
        bookSaveModel.setId(book.getId());
        bookSaveModel.setTitle(book.getTitle());
        bookSaveModel.setAuthorId(book.getAuthor().getId());
        bookSaveModel.setGenreIds(book.getGenres().stream().map(Genre::getId).toList());
        modelAndView.addObject("book_save", bookSaveModel);

        modelAndView.addObject("authors", authorService.findAll());
        modelAndView.addObject("genres", genreService.findAll());

        return modelAndView;
    }

    @PutMapping("books/{bookId}")
    public String updateBook(@PathVariable("bookId") String bookId, @ModelAttribute("book_save") BookSaveModel book) {
        Book savedBook = bookService.update(bookId, book.getTitle(), book.getAuthorId(), new HashSet<>(book.getGenreIds()));

        return String.format("redirect:/books/%s", savedBook.getId());
    }

    @DeleteMapping("/books/{bookId}")
    public String deleteBook(@PathVariable("bookId") String bookId) {
        bookService.deleteById(bookId);

        return "redirect:/books";
    }

    @PostMapping("/books/{bookId}/comments")
    public String addComment(@PathVariable("bookId") String bookId,
                             @ModelAttribute("commentSaveModel") CommentSaveModel commentSaveModel) {
        var savedComment = commentService.addComment(bookId, commentSaveModel.getText());

        return String.format("redirect:/books/%s", bookId);
    }
}
