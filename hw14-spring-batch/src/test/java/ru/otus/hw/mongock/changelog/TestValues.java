package ru.otus.hw.mongock.changelog;

import ru.otus.hw.mongo.models.Author;
import ru.otus.hw.mongo.models.Book;
import ru.otus.hw.mongo.models.Comment;
import ru.otus.hw.mongo.models.Genre;

import java.util.List;

public class TestValues {

    public static final Author AUTHOR_1 = new Author("1L", "Author 1");
    public static final Author AUTHOR_2 = new Author("2L", "Author 2");
    public static final Author AUTHOR_3 = new Author("3L", "Author 3");
    public static final Author AUTHOR_4 = new Author("4L", "Author 4");

    public static final List<Author> AUTHORS = List.of(AUTHOR_1, AUTHOR_2, AUTHOR_3, AUTHOR_4);

    public static final Genre GENRE_1 = new Genre("1L", "Genre 1");
    public static final Genre GENRE_2 = new Genre("2L", "Genre 2");
    public static final Genre GENRE_3 = new Genre("3L", "Genre 3");
    public static final Genre GENRE_4 = new Genre("4L", "Genre 4");
    public static final Genre GENRE_5 = new Genre("5L", "Genre 5");

    public static final List<Genre> GENRES = List.of(GENRE_1, GENRE_2, GENRE_3, GENRE_4, GENRE_5);

    public static final Book BOOK_1 = new Book("1L", "Book 1", AUTHOR_1, List.of(GENRE_1, GENRE_2, GENRE_3));
    public static final Book BOOK_2 = new Book("2L", "Book 2", AUTHOR_2, List.of(GENRE_3, GENRE_4));
    public static final Book BOOK_3 = new Book("3L", "Book 3", AUTHOR_3, List.of(GENRE_5));

    public static final List<Book> BOOKS = List.of(BOOK_1, BOOK_2, BOOK_3);

    public static final Comment COMMENT_1_1 = new Comment("11L", BOOK_1, "Comment 1 1");
    public static final Comment COMMENT_1_2 = new Comment("12L", BOOK_1, "Comment 1 2");
    public static final Comment COMMENT_1_3 = new Comment("13L", BOOK_1, "Comment 1 3");
    public static final Comment COMMENT_2_1 = new Comment("21L", BOOK_2, "Comment 2 1");
    public static final Comment COMMENT_2_2 = new Comment("22L", BOOK_2, "Comment 2 2");
    public static final Comment COMMENT_3_1 = new Comment("31L", BOOK_3, "Comment 3 1");

    public static final List<Comment> COMMENTS = List.of(COMMENT_1_1, COMMENT_1_2, COMMENT_1_3, COMMENT_2_1, COMMENT_2_2, COMMENT_3_1);

}
