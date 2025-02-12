package ru.otus.hw.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = BookRestController.class)
@TestPropertySource(properties = {"mongock.enabled=false"})
class BookRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

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

    private GenreDto genre1 = new GenreDto("1g", "Genre_1");
    private GenreDto genre2 = new GenreDto("2g", "Genre_2");
    private GenreDto genre3 = new GenreDto("3g", "Genre_3");

    private BookDto book1 = new BookDto("1b", "Book_1", author1, List.of(genre1));
    private BookDto book2 = new BookDto("2b", "Book_2", author2, List.of(genre2, genre3));
    private List<BookDto> books = List.of(book1, book2);

    @Test
    void findAllBooks() throws Exception {
        when(bookService.findAll()).thenReturn(books);

        mvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(books)));
    }
}