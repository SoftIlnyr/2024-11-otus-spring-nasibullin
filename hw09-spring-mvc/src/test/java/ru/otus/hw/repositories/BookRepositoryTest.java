package ru.otus.hw.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.mongock.listeners.MongoBookCascadeDeleteEventListener;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@Import(MongoBookCascadeDeleteEventListener.class)
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MongoOperations mongoOperations;

    @Test
    void deleteBook_deletesComments() {
        var author = authorRepository.save(new Author("TestAuthor"));
        var genre = genreRepository.save(new Genre("TestGenre"));
        var book = bookRepository.save(new Book("TestTitle", author, List.of(genre)));
        var comment1 = commentRepository.save(new Comment(book, "TestComment1"));
        var comment2 = commentRepository.save(new Comment(book, "TestComment2"));

        bookRepository.deleteById(book.getId());

        List<Comment> comments = mongoOperations.find(Query.query(Criteria.where("bookId").is(book.getId())), Comment.class);

        assertEquals(0, comments.size());

    }

}