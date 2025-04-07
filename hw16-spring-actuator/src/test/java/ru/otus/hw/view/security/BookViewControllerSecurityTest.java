package ru.otus.hw.view.security;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.config.SecurityConfig;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.security.RoleCheckService;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;
import ru.otus.hw.view.BookViewController;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(value = BookViewController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = {"mongock.enabled=false"})
class BookViewControllerSecurityTest {

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

    @MockitoBean
    private RoleCheckService roleCheckService;

    private AuthorDto author1 = new AuthorDto("1a", "Author_1");

    private GenreDto genre1 = new GenreDto("1g", "Genre_1");

    private BookDto book1 = new BookDto("1b", "Book_1", author1, List.of(genre1));

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
    void findAll() throws Exception {
        mvc.perform(get("/books/"))
                .andExpect(view().name("books"));
    }

    @WithMockUser(username = "user")
    @Test
    void getBookInfo() throws Exception {
        when(bookService.findById(book1.getId())).thenReturn(book1);

        mvc.perform(get("/books/" + book1.getId()))
                .andExpect(view().name("books_detail"))
                .andExpect(model().attribute("book", book1))
                .andExpect(model().attributeExists("comment_form"));
    }

    @WithMockUser(username = "user")
    @Test
    void getBookActions() throws Exception {

        when(bookService.findById(book1.getId())).thenReturn(book1);

        mvc.perform(get("/books/" + book1.getId() + "/actions"))
                .andExpect(view().name("books_actions"));
    }

    @WithAnonymousUser
    @Test
    void findAll_anonymous() throws Exception {
        mvc.perform(get("/books/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @WithAnonymousUser
    @Test
    void getBookInfo_anonymous() throws Exception {
        mvc.perform(get("/books/" + book1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @WithAnonymousUser
    @Test
    void getBookActions_anonymous() throws Exception {
        mvc.perform(get("/books/" + book1.getId() + "/actions"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

}