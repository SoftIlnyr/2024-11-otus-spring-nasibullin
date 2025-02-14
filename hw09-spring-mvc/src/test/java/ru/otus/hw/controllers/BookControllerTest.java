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
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
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

    private AuthorDto author1 = new AuthorDto("1a", "Author_1");
    private AuthorDto author2 = new AuthorDto("2a", "Author_2");
    private List<AuthorDto> authors = List.of(author1, author2);

    private GenreDto genre1 = new GenreDto("1g", "Genre_1");
    private GenreDto genre2 = new GenreDto("2g", "Genre_2");
    private GenreDto genre3 = new GenreDto("3g", "Genre_3");
    private List<GenreDto> genres = List.of(genre1, genre2, genre3);

    private BookDto book1 = new BookDto("1b", "Book_1", author1, List.of(genre1));
    private BookDto book2 = new BookDto("2b", "Book_2", author2, List.of(genre2, genre3));
    private List<BookDto> books = List.of(book1, book2);

    private CommentDto comment11 = new CommentDto("11c", book1, "Comment_1_1");
    private CommentDto comment12 = new CommentDto("12c", book1, "Comment_1_2");
    private CommentDto comment21 = new CommentDto("21c", book2, "Comment_2_1");

    @Captor
    ArgumentCaptor<BookCreateDto> bookCreateDtoCaptor;

    @Captor
    ArgumentCaptor<BookUpdateDto> bookUpdateDtoCaptor;

    @Captor
    ArgumentCaptor<String> bookIdCaptor;

    @Captor
    ArgumentCaptor<CommentCreateDto> commentCreateDtoCaptor;

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
                .andExpect(model().attributeExists("book_form"));
    }

    @Test
    void addNewBook() throws Exception {
        BookCreateDto bookCreateDto = new BookCreateDto();
        bookCreateDto.setTitle(book1.getTitle());
        bookCreateDto.setAuthorId(book1.getAuthor().getId());
        bookCreateDto.setGenreIds(book1.getGenres().stream().map(genre -> genre.getId()).collect(Collectors.toList()));

        when(bookService.insert(bookCreateDtoCaptor.capture())).thenReturn(book1);
        RequestBuilder request = post("/books").flashAttr("book_form", bookCreateDto);
        mvc.perform(request)
                .andExpect(view().name("redirect:/books/" + book1.getId()));

        BookCreateDto capturedValue = bookCreateDtoCaptor.getValue();
        assertNotNull(capturedValue);
        assertEquals(book1.getTitle(), capturedValue.getTitle());
        assertEquals(book1.getAuthor().getId(), capturedValue.getAuthorId());
        assertEquals(book1.getGenres().size(), capturedValue.getGenreIds().size());

        assertTrue(book1.getGenres().stream().map(genre -> genre.getId()).toList().containsAll(capturedValue.getGenreIds()));
    }

    @Test
    void getBookInfo() throws Exception {
        List<CommentDto> bookComments = List.of(comment11, comment12);
        when(bookService.findById(book1.getId())).thenReturn(book1);
        when(commentService.findAllCommentsByBookId(book1.getId())).thenReturn(bookComments);

        mvc.perform(get("/books/" + book1.getId()))
                .andExpect(view().name("books_detail"))
                .andExpect(model().attribute("book", book1))
                .andExpect(model().attribute("comments", bookComments))
                .andExpect(model().attributeExists("comment_form"));
    }

    @Test
    void getBookActions() throws Exception {

        BookUpdateDto bookUpdateDto = new BookUpdateDto();
        bookUpdateDto.setId(book1.getId());
        bookUpdateDto.setTitle(book1.getTitle());
        bookUpdateDto.setAuthorId(book1.getAuthor().getId());
        bookUpdateDto.setGenreIds(book1.getGenres().stream().map(genre -> genre.getId()).toList());

        when(bookService.findById(book1.getId())).thenReturn(book1);

        when(authorService.findAll()).thenReturn(authors);
        when(genreService.findAll()).thenReturn(genres);

        mvc.perform(get("/books/" + book1.getId() + "/actions"))
                .andExpect(view().name("books_actions"))
                .andExpect(model().attribute("authors", authors))
                .andExpect(model().attribute("genres", genres))
                .andExpect(model().attribute("book_form", bookUpdateDto));
    }

    @Test
    void updateBook() throws Exception {
        BookUpdateDto bookUpdateDto = new BookUpdateDto();
        bookUpdateDto.setId(book1.getId());
        bookUpdateDto.setTitle(book1.getTitle());
        bookUpdateDto.setAuthorId(book1.getAuthor().getId());
        bookUpdateDto.setGenreIds(book1.getGenres().stream().map(genre -> genre.getId()).collect(Collectors.toList()));

        when(bookService.update(bookUpdateDtoCaptor.capture())).thenReturn(book1);

        RequestBuilder request = put("/books/" + book1.getId()).flashAttr("book_form", bookUpdateDto);

        mvc.perform(request)
                .andExpect(view().name("redirect:/books/" + book1.getId()));

        BookUpdateDto capturedValue = bookUpdateDtoCaptor.getValue();
        assertEquals(book1.getId(), capturedValue.getId());
        assertEquals(book1.getTitle(), capturedValue.getTitle());
        assertEquals(book1.getAuthor().getId(), capturedValue.getAuthorId());
        assertEquals(book1.getGenres().size(), capturedValue.getGenreIds().size());

        assertTrue(book1.getGenres().stream().map(genre -> genre.getId()).toList().containsAll(capturedValue.getGenreIds()));
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
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setBookId(book1.getId());
        String commentText = "text";
        commentCreateDto.setText(commentText);

        RequestBuilder request = post("/books/" + book1.getId() + "/comments")
                .flashAttr("comment_form", commentCreateDto);

        mvc.perform(request)
                .andExpect(view().name("redirect:/books/" + book1.getId()));

        verify(commentService).addComment(commentCreateDtoCaptor.capture());

        CommentCreateDto capturedValue = commentCreateDtoCaptor.getValue();

        assertNotNull(capturedValue);
        assertEquals(book1.getId(), capturedValue.getBookId());
        assertEquals(commentText, capturedValue.getText());
    }

    @Test
    void getBookInfo_notFound() throws Exception {
        when(bookService.findById(anyString())).thenThrow(EntityNotFoundException.class);

        mvc.perform(get("/books/" + "badBookId"))
                .andExpect(view().name("not_found"));
    }

    @Test
    void getBookInfo_serverException() throws Exception {
        when(bookService.findById(anyString())).thenThrow(RuntimeException.class);

        mvc.perform(get("/books/" + "badBookId"))
                .andExpect(view().name("server_error"));
    }
}