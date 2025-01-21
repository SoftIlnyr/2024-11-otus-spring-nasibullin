package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookComment;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookCommentRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookCommentRepository bookCommentRepository;

    @Transactional(readOnly = true)
    @Override
    public Optional<Book> findById(long id) {
        return bookRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Transactional
    @Override
    public Book insert(String title, long authorId, Set<Long> genresIds) {
        return save(0, title, authorId, genresIds);
    }

    @Transactional
    @Override
    public Book update(long id, String title, long authorId, Set<Long> genresIds) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        if (bookOptional.isEmpty()) {
            throw new EntityNotFoundException("Book with id %s not found".formatted(id));
        }
        return save(id, title, authorId, genresIds, bookOptional.get().getComments());
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        bookRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookComment> findAllComments() {
        return bookCommentRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookComment> findAllCommentsByBookId(long bookId) {
        return bookCommentRepository.findByBookId(bookId);
    }

    @Transactional
    @Override
    public BookComment addComment(long bookId, String comment) {
        Optional<Book> book = bookRepository.findById(bookId);
        if (book.isEmpty()) {
            throw new EntityNotFoundException("Book with id %s not found".formatted(bookId));
        }
        BookComment bookComment = new BookComment(0, book.get(), comment);
        return bookCommentRepository.save(bookComment);
    }

    @Transactional
    @Override
    public BookComment updateComment(long bookCommentId, String comment) {
        Optional<BookComment> bookComment = bookCommentRepository.findById(bookCommentId);
        if (bookComment.isEmpty()) {
            throw new EntityNotFoundException("BookComment with id %s not found".formatted(bookCommentId));
        }
        bookComment.get().setText(comment);
        bookCommentRepository.save(bookComment.get());
        return bookCommentRepository.findById(bookCommentId).get();
    }

    @Transactional
    @Override
    public void deleteCommentById(long bookCommentId) {
        bookCommentRepository.deleteById(bookCommentId);
    }

    private Book save(long id, String title, long authorId, Set<Long> genresIds) {
        return save(id, title, authorId, genresIds, null);
    }

    private Book save(long id, String title, long authorId, Set<Long> genresIds, List<BookComment> bookComments) {
        if (isEmpty(genresIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }

        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));
        var genres = genreRepository.findAllByIds(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }

        var book = new Book(id, title, author, genres, bookComments);
        return bookRepository.save(book);
    }

}
