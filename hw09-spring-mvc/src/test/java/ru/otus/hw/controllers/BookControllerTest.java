package ru.otus.hw.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookSaveModel;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.CommentSaveModel;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(value = BookController.class)
@TestPropertySource(properties = {"mongock.enabled=false"})
class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private CommentService commentService;

    private Author author1 = new Author("1a", "Author_1");
    private Author author2 = new Author("2a", "Author_2");
    private List<Author> authors = List.of(author1, author2);

    private Genre genre1 = new Genre("1g", "Genre_1");
    private Genre genre2 = new Genre("2g", "Genre_2");
    private Genre genre3 = new Genre("3g", "Genre_3");
    private List<Genre> genres = List.of(genre1, genre2, genre3);

    private Book book1 = new Book("1b", "Book_1", author1, List.of(genre1));
    private Book book2 = new Book("2b", "Book_2", author2, List.of(genre2, genre3));
    private List<Book> books = List.of(book1, book2);

    private Comment comment11 = new Comment("11c", book1, "Comment_1_1");
    private Comment comment12 = new Comment("12c", book1, "Comment_1_2");
    private Comment comment21 = new Comment("21c", book2, "Comment_2_1");

    @Captor
    ArgumentCaptor<String> bookIdCaptor;

    @Captor
    ArgumentCaptor<String> bookTitleCaptor;

    @Captor
    ArgumentCaptor<String> authorIdCaptor;

    @Captor
    ArgumentCaptor<HashSet<String>> genreIdCaptor;

    @Test
    void findAll() throws Exception {
        when(bookService.findAll()).thenReturn(books);
        when(authorService.findAll()).thenReturn(authors);
        when(genreService.findAll()).thenReturn(genres);

        mvc.perform(get("/books/"))
                .andExpect(view().name("books"))
                .andExpect(model().attribute("books", books))
                .andExpect(model().attribute("authors", authors))
                .andExpect(model().attribute("genres", genres))
                .andExpect(model().attributeExists("book_save"));
    }

    @Test
    void addNewBook() throws Exception {
        BookSaveModel bookSaveModel = new BookSaveModel();
        bookSaveModel.setTitle(book1.getTitle());
        bookSaveModel.setAuthorId(book1.getAuthor().getId());
        bookSaveModel.setGenreIds(book1.getGenres().stream().map(Genre::getId).collect(Collectors.toList()));

        when(bookService.insert(bookTitleCaptor.capture(), authorIdCaptor.capture(), genreIdCaptor.capture()))
                .thenReturn(book1);
        RequestBuilder request = post("/books").flashAttr("book_save", bookSaveModel);
        mvc.perform(request)
                .andExpect(view().name("redirect:/books/" + book1.getId()));

        assertEquals(book1.getTitle(), bookTitleCaptor.getValue());
        assertEquals(book1.getAuthor().getId(), authorIdCaptor.getValue());
        assertEquals(book1.getGenres().size(), genreIdCaptor.getValue().size());

        assertTrue(book1.getGenres().stream().map(Genre::getId).toList().containsAll(genreIdCaptor.getValue()));
    }

    @Test
    void getBookInfo() throws Exception {
        List<Comment> bookComments = List.of(comment11, comment12);
        when(bookService.findById(book1.getId())).thenReturn(Optional.of(book1));
        when(commentService.findAllCommentsByBookId(book1.getId())).thenReturn(bookComments);

        mvc.perform(get("/books/" + book1.getId()))
                .andExpect(view().name("books_detail"))
                .andExpect(model().attribute("book", book1))
                .andExpect(model().attribute("comments", bookComments))
                .andExpect(model().attributeExists("commentSaveModel"));
    }

    @Test
    void getBookActions() throws Exception {

        BookSaveModel bookSaveModel = new BookSaveModel();
        bookSaveModel.setId(book1.getId());
        bookSaveModel.setTitle(book1.getTitle());
        bookSaveModel.setAuthorId(book1.getAuthor().getId());
        bookSaveModel.setGenreIds(book1.getGenres().stream().map(Genre::getId).toList());

        when(bookService.findById(book1.getId())).thenReturn(Optional.of(book1));

        when(authorService.findAll()).thenReturn(authors);
        when(genreService.findAll()).thenReturn(genres);

        mvc.perform(get("/books/" + book1.getId() + "/actions"))
                .andExpect(view().name("books_actions"))
                .andExpect(model().attribute("authors", authors))
                .andExpect(model().attribute("genres", genres))
                .andExpect(model().attribute("book_save", bookSaveModel));
    }

    @Test
    void updateBook() throws Exception {
        BookSaveModel bookSaveModel = new BookSaveModel();
        bookSaveModel.setTitle(book1.getTitle());
        bookSaveModel.setAuthorId(book1.getAuthor().getId());
        bookSaveModel.setGenreIds(book1.getGenres().stream().map(Genre::getId).collect(Collectors.toList()));

        when(bookService.update(bookIdCaptor.capture(), bookTitleCaptor.capture(), authorIdCaptor.capture(), genreIdCaptor.capture()))
                .thenReturn(book1);

        RequestBuilder request = put("/books/" + book1.getId()).flashAttr("book_save", bookSaveModel);

        mvc.perform(request)
                .andExpect(view().name("redirect:/books/" + book1.getId()));

        assertEquals(book1.getId(), bookIdCaptor.getValue());
        assertEquals(book1.getTitle(), bookTitleCaptor.getValue());
        assertEquals(book1.getAuthor().getId(), authorIdCaptor.getValue());
        assertEquals(book1.getGenres().size(), genreIdCaptor.getValue().size());

        assertTrue(book1.getGenres().stream().map(Genre::getId).toList().containsAll(genreIdCaptor.getValue()));
    }

    @Test
    void deleteBook() throws Exception {
        mvc.perform(delete("/books/" + book1.getId()))
                .andExpect(view().name("redirect:/books"));

        verify(bookService).deleteById(bookIdCaptor.capture());

        assertEquals(book1.getId(), bookIdCaptor.getValue());
    }

    @Test
    void addComment() throws Exception {
        CommentSaveModel commentSaveModel = new CommentSaveModel();
        commentSaveModel.setBookId(book1.getId());
        String commentText = "text";
        commentSaveModel.setText(commentText);

        ArgumentCaptor<String> commentTextCaptor = ArgumentCaptor.forClass(String.class);

        RequestBuilder request = post("/books/" + book1.getId() + "/comments")
                .flashAttr("commentSaveModel", commentSaveModel);

        mvc.perform(request)
                .andExpect(view().name("redirect:/books/" + book1.getId()));

        verify(commentService).addComment(bookIdCaptor.capture(), commentTextCaptor.capture());

        assertEquals(book1.getId(), bookIdCaptor.getValue());
        assertEquals(commentText, commentTextCaptor.getValue());
    }
}