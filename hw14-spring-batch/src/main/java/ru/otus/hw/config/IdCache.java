package ru.otus.hw.config;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IdCache {

    private final Map<String, Long> authorCache = new ConcurrentHashMap<>();

    private final Map<String, Long> bookCache = new ConcurrentHashMap<>();

    private final Map<String, Long> genreCache = new ConcurrentHashMap<>();

    private final Map<String, Long> commentCache = new ConcurrentHashMap<>();

    public void putAuthor(String authorMongoId, long authorPostgresId) {
        authorCache.put(authorMongoId, authorPostgresId);
    }

    public void putGenre(String genreMongoId, long genrePostgresId) {
        genreCache.put(genreMongoId, genrePostgresId);
    }

    public void putBook(String bookMongoId, long bookPostgresId) {
        bookCache.put(bookMongoId, bookPostgresId);
    }

    public void putComment(String commentMongoId, long commentPostgresId) {
        commentCache.put(commentMongoId, commentPostgresId);
    }

    public Long getAuthor(String authorMongoId) {
        return authorCache.get(authorMongoId);
    }

    public Long getGenre(String genreMongoId) {
        return genreCache.get(genreMongoId);
    }

    public Long getBook(String bookMongoId) {
        return bookCache.get(bookMongoId);
    }

    public Long getComment(String commentMongoId) {
        return commentCache.get(commentMongoId);
    }

}
