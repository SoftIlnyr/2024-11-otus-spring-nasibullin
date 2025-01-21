package ru.otus.hw.repositories;

import ru.otus.hw.models.BookComment;

import java.util.List;
import java.util.Optional;

public interface BookCommentRepository {

    Optional<BookComment> findById(long id);

    List<BookComment> findAll();

    List<BookComment> findByBookId(long bookId);

    BookComment save(BookComment bookComment);

    void deleteById(long id);
}
