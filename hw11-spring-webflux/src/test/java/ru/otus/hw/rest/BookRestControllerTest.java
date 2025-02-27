package ru.otus.hw.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.reactive.function.client.WebClient;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.GenreDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static ru.otus.hw.mongock.changelog.TestValues.AUTHOR_1;
import static ru.otus.hw.mongock.changelog.TestValues.AUTHOR_2;
import static ru.otus.hw.mongock.changelog.TestValues.AUTHOR_3;
import static ru.otus.hw.mongock.changelog.TestValues.AUTHOR_4;
import static ru.otus.hw.mongock.changelog.TestValues.BOOKS;
import static ru.otus.hw.mongock.changelog.TestValues.BOOK_1;
import static ru.otus.hw.mongock.changelog.TestValues.BOOK_2;
import static ru.otus.hw.mongock.changelog.TestValues.BOOK_3;
import static ru.otus.hw.mongock.changelog.TestValues.GENRE_1;
import static ru.otus.hw.mongock.changelog.TestValues.GENRE_2;
import static ru.otus.hw.mongock.changelog.TestValues.GENRE_3;
import static ru.otus.hw.mongock.changelog.TestValues.GENRE_4;
import static ru.otus.hw.mongock.changelog.TestValues.GENRE_5;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookRestControllerTest {

    @LocalServerPort
    private int port;

    private WebClient webClient;


    private final AuthorDto authorDto1 = new AuthorDto(AUTHOR_1.getId(), AUTHOR_1.getFullName());

    private final AuthorDto authorDto2 = new AuthorDto(AUTHOR_2.getId(), AUTHOR_2.getFullName());

    private final AuthorDto authorDto3 = new AuthorDto(AUTHOR_3.getId(), AUTHOR_3.getFullName());

    private final AuthorDto authorDto4 = new AuthorDto(AUTHOR_4.getId(), AUTHOR_4.getFullName());


    private GenreDto genreDto1 = new GenreDto(GENRE_1.getId(), GENRE_1.getName());

    private GenreDto genreDto2 = new GenreDto(GENRE_2.getId(), GENRE_2.getName());

    private GenreDto genreDto3 = new GenreDto(GENRE_3.getId(), GENRE_3.getName());

    private GenreDto genreDto4 = new GenreDto(GENRE_4.getId(), GENRE_4.getName());

    private GenreDto genreDto5 = new GenreDto(GENRE_5.getId(), GENRE_5.getName());


    private BookDto bookDto1 = new BookDto(BOOK_1.getId(), BOOK_1.getTitle(), authorDto1,
            List.of(genreDto1, genreDto2, genreDto3));

    private BookDto bookDto2 = new BookDto(BOOK_2.getId(), BOOK_2.getTitle(), authorDto2, List.of(genreDto3, genreDto4));

    private BookDto bookDto3 = new BookDto(BOOK_3.getId(), BOOK_3.getTitle(), authorDto3, List.of(genreDto5));

    @BeforeEach
    void setUp() {
        webClient = WebClient.create(String.format("http://localhost:%d", port));
    }

    @Test
    void findAllBooks() {
        var expectedSize = BOOKS.size();

        List<BookDto> result = webClient
                .get().uri("/api/books")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(BookDto.class)
                .take(expectedSize)
                .collectList()
                .block();

        assertThat(result)
                .hasSize(expectedSize)
                .contains(bookDto1, bookDto2, bookDto3);
    }

    @Test
    void addNewBook() {

        BookCreateDto bookCreateDto = new BookCreateDto();
        bookCreateDto.setTitle("Test title");
        bookCreateDto.setAuthorId(authorDto4.getId());
        bookCreateDto.setGenreIds(List.of(genreDto2.getId(), genreDto4.getId()));

        BookDto savedBook = webClient.post()
                .uri("/api/books")
                .bodyValue(bookCreateDto)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(BookDto.class)
                .block();

        assertNotNull(savedBook);
        assertEquals(bookCreateDto.getTitle(), savedBook.getTitle());
        assertEquals(authorDto4, savedBook.getAuthor());
        assertThat(savedBook.getGenres()).containsExactly(genreDto2, genreDto4);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void updateBook() {
        BookUpdateDto bookUpdateDto = new BookUpdateDto();
        bookUpdateDto.setId(bookDto1.getId());
        bookUpdateDto.setTitle("Test title");
        bookUpdateDto.setAuthorId(authorDto4.getId());
        bookUpdateDto.setGenreIds(List.of(genreDto2.getId(), genreDto4.getId()));

        BookDto savedBook = webClient.put()
                .uri("/api/books/" + bookDto1.getId())
                .bodyValue(bookUpdateDto)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(BookDto.class)
                .block();

        assertNotNull(savedBook);
        assertEquals(bookUpdateDto.getId(), savedBook.getId());
        assertEquals(bookUpdateDto.getTitle(), savedBook.getTitle());
        assertEquals(authorDto4, savedBook.getAuthor());
        assertThat(savedBook.getGenres()).containsExactly(genreDto2, genreDto4);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void deleteBook() {
        webClient.delete()
                .uri("/api/books/" + bookDto1.getId())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @Test
    void addComment() {
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setBookId(bookDto1.getId());
        commentCreateDto.setText("Test comment");

        CommentDto result = webClient
                .post().uri("/api/books/" + bookDto1.getId() + "/comments")
                .bodyValue(commentCreateDto)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(CommentDto.class)
                .block();

        assertNotNull(result);
        assertEquals(commentCreateDto.getText(), result.getText());
    }
}
