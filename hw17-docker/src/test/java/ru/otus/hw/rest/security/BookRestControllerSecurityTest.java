package ru.otus.hw.rest.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import ru.otus.hw.config.SecurityConfig;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.rest.BookRestController;
import ru.otus.hw.rest.GlobalExceptionHandler;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = BookRestController.class)
@Import({SecurityConfig.class})
@TestPropertySource(properties = {"mongock.enabled=false"})
class BookRestControllerSecurityTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    @WithMockUser(username = "user")
    @Test
    void findAllBooks() throws Exception {
        mvc.perform(get("/api/books"))
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "user")
    @Test
    void addNewBook() throws Exception {
        BookCreateDto bookCreateDto = new BookCreateDto();
        bookCreateDto.setTitle(book1.getTitle());
        bookCreateDto.setAuthorId(book1.getAuthor().getId());
        bookCreateDto.setGenreIds(book1.getGenres().stream().map(genre -> genre.getId()).collect(Collectors.toList()));

        when(bookService.insert(bookCreateDtoCaptor.capture())).thenReturn(book1);

        RequestBuilder request = post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookCreateDto));
        mvc.perform(request)
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "user")
    @Test
    void updateBook() throws Exception {
        BookUpdateDto bookUpdateDto = new BookUpdateDto();
        bookUpdateDto.setId(book1.getId());
        bookUpdateDto.setTitle(book1.getTitle());
        bookUpdateDto.setAuthorId(book1.getAuthor().getId());
        bookUpdateDto.setGenreIds(book1.getGenres().stream().map(genre -> genre.getId()).collect(Collectors.toList()));

        when(bookService.update(bookUpdateDtoCaptor.capture())).thenReturn(book1);

        RequestBuilder request = put("/api/books/" + book1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookUpdateDto));

        mvc.perform(request)
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "user")
    @Test
    void deleteBook() throws Exception {
        mvc.perform(delete("/api/books/" + book1.getId()))
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "user")
    @Test
    void addComment() throws Exception {
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setBookId(book1.getId());
        String commentText = "text";
        commentCreateDto.setText(commentText);

        RequestBuilder request = post("/api/books/" + book1.getId() + "/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentCreateDto));

        mvc.perform(request)
                .andExpect(status().isOk());
    }

    @WithAnonymousUser
    @Test
    void findAllBooks_anonymous() throws Exception {
        when(bookService.findAll()).thenReturn(books);

        mvc.perform(get("/api/books"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @WithAnonymousUser
    @Test
    void addNewBook_anonymous() throws Exception {
        BookCreateDto bookCreateDto = new BookCreateDto();
        bookCreateDto.setTitle(book1.getTitle());
        bookCreateDto.setAuthorId(book1.getAuthor().getId());
        bookCreateDto.setGenreIds(book1.getGenres().stream().map(genre -> genre.getId()).collect(Collectors.toList()));

        when(bookService.insert(bookCreateDtoCaptor.capture())).thenReturn(book1);

        RequestBuilder request = post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookCreateDto));
        mvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @WithAnonymousUser
    @Test
    void deleteBook_anonymous() throws Exception {
        mvc.perform(delete("/api/books/" + book1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @WithAnonymousUser
    @Test
    void addComment_anonymous() throws Exception {
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setBookId(book1.getId());
        String commentText = "text";
        commentCreateDto.setText(commentText);

        RequestBuilder request = post("/api/books/" + book1.getId() + "/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentCreateDto));

        mvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}