package ru.otus.hw.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.stream.Stream;

@ChangeLog(order = "001")
public class InitChangelog {

    private Author author1;

    private Author author2;

    private Author author3;


    private Genre genre1;

    private Genre genre2;

    private Genre genre3;

    private Genre genre4;

    private Genre genre5;

    private Genre genre6;


    private Book book1;

    private Book book2;

    private Book book3;


    private Comment comment11;

    private Comment comment12;

    private Comment comment13;

    private Comment comment21;

    private Comment comment31;

    private Comment comment32;


    @ChangeSet(order = "001", id = "001_drop_database", author = "softi", runAlways = true)
    public void dropDatabase(MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "002", id = "002_init_authors", author = "softi", runAlways = true)
    public void initAuthors(AuthorRepository authorRepository) {
        author1 = authorRepository.save(new Author("Author_1")).block();
        author2 = authorRepository.save(new Author("Author_2")).block();
        author3 = authorRepository.save(new Author("Author_3")).block();
    }

    @ChangeSet(order = "003", id = "003_init_genres", author = "softi", runAlways = true)
    public void initGenres(GenreRepository genreRepository) {
        genre1 = genreRepository.save(new Genre("Genre_1")).block();
        genre2 = genreRepository.save(new Genre("Genre_2")).block();
        genre3 = genreRepository.save(new Genre("Genre_3")).block();
        genre4 = genreRepository.save(new Genre("Genre_4")).block();
        genre5 = genreRepository.save(new Genre("Genre_5")).block();
        genre6 = genreRepository.save(new Genre("Genre_6")).block();
    }

    @ChangeSet(order = "004", id = "004_init_books", author = "softi", runAlways = true)
    public void initBooks(BookRepository bookRepository) {
        book1 = bookRepository.save(new Book("Book_1", author1, Stream.of(genre1, genre2).toList())).block();
        book2 = bookRepository.save(new Book("Book_2", author2, Stream.of(genre3, genre4).toList())).block();
        book3 = bookRepository.save(new Book("Book_3", author3, Stream.of(genre5, genre6).toList())).block();
    }

    @ChangeSet(order = "005", id = "005_init_comments", author = "softi", runAlways = true)
    public void initComments(CommentRepository commentRepository) {
        comment11 = commentRepository.save(new Comment(book1, "Comment_1_1")).block();
        comment12 = commentRepository.save(new Comment(book1, "Comment_1_2")).block();
        comment12 = commentRepository.save(new Comment(book1, "Comment_1_3")).block();
        comment21 = commentRepository.save(new Comment(book2, "Comment_2_1")).block();
        comment31 = commentRepository.save(new Comment(book3, "Comment_3_1")).block();
        comment31 = commentRepository.save(new Comment(book3, "Comment_3_2")).block();
    }

}
