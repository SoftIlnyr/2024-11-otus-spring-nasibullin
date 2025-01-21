package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.BookComment;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Repository
@RequiredArgsConstructor
public class JpaBookCommentRepository implements BookCommentRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public Optional<BookComment> findById(long id) {
        TypedQuery<BookComment> query = em.createQuery("select bc from BookComment bc where id = :id",
                BookComment.class);
        query.setParameter("id", id);
        query.setHint(FETCH.getKey(), em.getEntityGraph("book-comment-book-entity-graph"));
        return query.getResultList().stream().findFirst();
    }

    @Override
    public List<BookComment> findAll() {
        TypedQuery<BookComment> query = em.createQuery("select bc from BookComment bc", BookComment.class);
        query.setHint(FETCH.getKey(), em.getEntityGraph("book-comment-book-entity-graph"));
        return query.getResultList();
    }

    @Override
    public List<BookComment> findByBookId(long bookId) {
        TypedQuery<BookComment> query = em.createQuery("""
                select bc from BookComment bc
                where bc.book.id = :book_id
                """, BookComment.class);
        query.setParameter("book_id", bookId);
        query.setHint(FETCH.getKey(), em.getEntityGraph("book-comment-book-entity-graph"));
        return query.getResultList();
    }

    @Override
    public BookComment save(BookComment bookComment) {
        if (bookComment.getId() == 0) {
            em.persist(bookComment);
            return bookComment;
        }
        return em.merge(bookComment);
    }

    @Override
    public void deleteById(long id) {
        em.remove(em.find(BookComment.class, id));
    }
}
