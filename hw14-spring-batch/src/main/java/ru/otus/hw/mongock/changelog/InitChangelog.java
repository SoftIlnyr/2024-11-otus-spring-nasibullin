package ru.otus.hw.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.otus.hw.mongo.models.Author;
import ru.otus.hw.mongo.models.Book;
import ru.otus.hw.mongo.models.Comment;
import ru.otus.hw.mongo.models.Genre;
import ru.otus.hw.mongo.repositories.AuthorRepository;
import ru.otus.hw.mongo.repositories.BookRepository;
import ru.otus.hw.mongo.repositories.CommentRepository;
import ru.otus.hw.mongo.repositories.GenreRepository;

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
    public void initAuthors(@Qualifier("mongoAuthorRepository") AuthorRepository authorRepository) {
        author1 = authorRepository.save(new Author("Author_1"));
        author2 = authorRepository.save(new Author("Author_2"));
        author3 = authorRepository.save(new Author("Author_3"));
    }

    @ChangeSet(order = "003", id = "003_init_genres", author = "softi", runAlways = true)
    public void initGenres(@Qualifier("mongoGenreRepository") GenreRepository genreRepository) {
        genre1 = genreRepository.save(new Genre("Genre_1"));
        genre2 = genreRepository.save(new Genre("Genre_2"));
        genre3 = genreRepository.save(new Genre("Genre_3"));
        genre4 = genreRepository.save(new Genre("Genre_4"));
        genre5 = genreRepository.save(new Genre("Genre_5"));
        genre6 = genreRepository.save(new Genre("Genre_6"));
    }

    @ChangeSet(order = "004", id = "004_init_books", author = "softi", runAlways = true)
    public void initBooks(@Qualifier("mongoBookRepository") BookRepository bookRepository) {
        book1 = bookRepository.save(new Book("Book_1", author1, Stream.of(genre1, genre2).toList()));
        book2 = bookRepository.save(new Book("Book_2", author2, Stream.of(genre3, genre4).toList()));
        book3 = bookRepository.save(new Book("Book_3", author3, Stream.of(genre5, genre6).toList()));
    }

    @ChangeSet(order = "005", id = "005_init_comments", author = "softi", runAlways = true)
    public void initComments(@Qualifier("mongoCommentRepository") CommentRepository commentRepository) {
        comment11 = commentRepository.save(new Comment(book1, "Comment_1_1"));
        comment12 = commentRepository.save(new Comment(book1, "Comment_1_2"));
        comment12 = commentRepository.save(new Comment(book1, "Comment_1_3"));
        comment21 = commentRepository.save(new Comment(book2, "Comment_2_1"));
        comment31 = commentRepository.save(new Comment(book3, "Comment_3_1"));
        comment31 = commentRepository.save(new Comment(book3, "Comment_3_2"));
    }

}
