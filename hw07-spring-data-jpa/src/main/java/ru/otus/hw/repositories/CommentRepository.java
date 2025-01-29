package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(value = "comment-book-entity-graph")
    Optional<Comment> findById(long id);

    @EntityGraph(value = "comment-book-entity-graph")
    List<Comment> findAll();

    @EntityGraph(value = "comment-book-entity-graph")
    @Query("""
                    select c from Comment c
                    where c.book.id = :bookId
            """)
    List<Comment> findByBookId(@Param("bookId") long bookId);

}
