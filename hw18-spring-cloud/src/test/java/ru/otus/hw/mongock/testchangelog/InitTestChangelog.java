package ru.otus.hw.mongock.testchangelog;

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

@ChangeLog(order = "001")
public class InitTestChangelog {

    @ChangeSet(order = "001", id = "001_drop_database", author = "softi", runAlways = true)
    public void dropDatabase(MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "002", id = "002_init_authors", author = "softi", runAlways = true)
    public void initAuthors(AuthorRepository authorRepository) {
        for (Author author : TestValues.AUTHORS) {
            authorRepository.save(author);
        }
    }

    @ChangeSet(order = "003", id = "003_init_genres", author = "softi", runAlways = true)
    public void initGenres(GenreRepository genreRepository) {
        for (Genre genre : TestValues.GENRES) {
            genreRepository.save(genre);
        }
    }

    @ChangeSet(order = "004", id = "004_init_books", author = "softi", runAlways = true)
    public void initBooks(BookRepository bookRepository) {
        for (Book book : TestValues.BOOKS) {
            bookRepository.save(book);
        }
    }

    @ChangeSet(order = "005", id = "005_init_comments", author = "softi", runAlways = true)
    public void initComments(CommentRepository commentRepository) {
        for (Comment comment : TestValues.COMMENTS) {
            commentRepository.save(comment);
        }
    }

}
