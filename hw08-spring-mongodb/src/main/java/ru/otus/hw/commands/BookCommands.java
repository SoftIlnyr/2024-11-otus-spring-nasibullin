package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.models.Comment;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
@RequiredArgsConstructor
@ShellComponent
public class BookCommands {

    private final BookService bookService;

    private final BookConverter bookConverter;

    private final CommentService commentService;

    private final CommentConverter commentConverter;

    @ShellMethod(value = "Find all books", key = "ab")
    public String findAllBooks() {
        return bookService.findAll().stream()
                .map(bookConverter::bookToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Find book by id", key = "bbid")
    public String findBookById(String id) {
        return bookService.findById(id)
                .map(bookConverter::bookToString)
                .orElse("Book with id %s not found".formatted(id));
    }

    @ShellMethod(value = "Insert book", key = "bins")
    public String insertBook(String title, String authorId, Set<String> genresIds) {
        var savedBook = bookService.insert(title, authorId, genresIds);
        return bookConverter.bookToString(savedBook);
    }

    @ShellMethod(value = "Update book", key = "bupd")
    public String updateBook(String id, String title, String authorId, Set<String> genresIds) {
        var savedBook = bookService.update(id, title, authorId, genresIds);
        return bookConverter.bookToString(savedBook);
    }

    @ShellMethod(value = "Delete book by id", key = "bdel")
    public void deleteBook(String id) {
        bookService.deleteById(id);
    }

    @ShellMethod(value = "Find all book comments", key = "bca")
    public String findAllBookComments() {
        return commentService.findAllComments().stream()
                .map(commentConverter::commentToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Find all book comments for book", key = "bcbid")
    public String findAllBookCommentsByBookId(String bookId) {
        return commentService.findAllCommentsByBookId(bookId).stream()
                .map(commentConverter::commentToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Add comment to book", key = "bcins")
    public String addBookComment(String bookId, String comment) {
        Comment bookComment = commentService.addComment(bookId, comment);
        return commentConverter.commentToString(bookComment);
    }

    @ShellMethod(value = "Update comment to book", key = "bcupd")
    public String updateBookComment(String commentId, String comment) {
        Comment bookComment = commentService.updateComment(commentId, comment);
        return commentConverter.commentToString(bookComment);
    }

    @ShellMethod(value = "Delete book comment", key = "bcdel")
    public void deleteComment(String commentId) {
        commentService.deleteCommentById(commentId);
    }

}
