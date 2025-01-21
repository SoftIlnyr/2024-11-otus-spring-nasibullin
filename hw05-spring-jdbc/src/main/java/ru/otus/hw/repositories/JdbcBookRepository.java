package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final JdbcOperations jdbc;

    private final NamedParameterJdbcOperations namedJdbc;

    private final GenreRepository genreRepository;

    @Override
    public Optional<Book> findById(long id) {
        var book = jdbc.query("""
                select books.id as book_id, books.title as book_title, 
                       authors.id as author_id, authors.full_name as author_full_name,
                       books_genres.genre_id as genre_id, genres.name as genre_name
                from books
                left join authors on books.author_id = authors.id
                left join books_genres on books.id = books_genres.book_id
                left join genres on genres.id = books_genres.genre_id
                where books.id = ?
                """, new BookResultSetExtractor(), id);

        return Optional.ofNullable(book);
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var relations = getAllGenreRelations();
        var books = getAllBooksWithoutGenres();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        jdbc.update("delete from books where id = ?", id);
    }

    private List<Book> getAllBooksWithoutGenres() {
        return jdbc.query("""
                select books.id as book_id, books.title as book_title, 
                       authors.id as author_id, authors.full_name as author_full_name
                from books
                join authors on books.author_id = authors.id
                """, new BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        return jdbc.query("select book_id, genre_id from books_genres", new BookGenreRelationMapper());
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        var genresMap = genres.stream().collect(Collectors.toMap(Genre::getId, genre -> genre));
        var booksMap = booksWithoutGenres.stream().collect(Collectors.toMap(Book::getId, book -> book));
        relations.forEach(relation -> booksMap.get(relation.bookId).getGenres().add(genresMap.get(relation.genreId)));
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title", book.getTitle());
        params.addValue("author_id", book.getAuthor().getId());

        namedJdbc.update("insert into books (title, author_id) values (:title, :author_id)",
                params, keyHolder, new String[]{"id"});

        //noinspection DataFlowIssue
        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {

        var numberOfUpdatedRows = namedJdbc.update("""
                update books set title = :title, author_id = :author_id where id = :id
                """,
                Map.of("id", book.getId(), "title", book.getTitle(), "author_id", book.getAuthor().getId()));

        // Выбросить EntityNotFoundException если не обновлено ни одной записи в БД
        if (numberOfUpdatedRows == 0) {
            throw new EntityNotFoundException(String.format("Book with id %d not found", book.getId()));
        }
        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        Map<String, Object>[] attributes = new HashMap[book.getGenres().size()];
        for (int i = 0; i < book.getGenres().size(); i++) {
            attributes[i] = new HashMap<>();
            attributes[i].put("book_id", book.getId());
            attributes[i].put("genre", book.getGenres().get(i).getId());
        }

        namedJdbc.batchUpdate("insert into books_genres (book_id, genre_id) values (:book_id, :genre)", attributes);
    }

    private void removeGenresRelationsFor(Book book) {
        namedJdbc.update("delete from books_genres where book_id = :book_id", Map.of("book_id", book.getId()));
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            Author author = new Author(rs.getLong("author_id"), rs.getString("author_full_name"));
            Book book = new Book(rs.getLong("book_id"), rs.getString("book_title"), author, new ArrayList<>());
            return book;
        }
    }

    // Использовать для findById
    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (!rs.isBeforeFirst()) {
                return null;
            }

            Book book = new Book();
            List<Genre> genres = new ArrayList<>();
            book.setGenres(genres);
            while (rs.next()) {
                if (book.getId() == 0) {
                    book.setId(rs.getLong("book_id"));
                    book.setGenres(genres);
                }
                if (book.getTitle() == null) {
                    book.setTitle(rs.getString("book_title"));
                }
                if (book.getAuthor() == null) {
                    Author author = new Author(rs.getLong("author_id"), rs.getString("author_full_name"));
                    book.setAuthor(author);
                }

                genres.add(new Genre(rs.getLong("genre_id"), rs.getString("genre_name")));
            }
            return book;
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }

    private static class BookGenreRelationMapper implements RowMapper<BookGenreRelation> {

        @Override
        public BookGenreRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new BookGenreRelation(rs.getLong("book_id"), rs.getLong("genre_id"));
        }
    }
}
