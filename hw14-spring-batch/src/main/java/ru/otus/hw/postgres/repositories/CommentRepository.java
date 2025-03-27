package ru.otus.hw.postgres.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.otus.hw.postgres.models.Comment;

import java.util.List;

@Repository("pgCommentRepository")
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
                    select c from Comment c
                    where c.book.id = :bookId
            """)
    List<Comment> findByBookId(@Param("bookId") long bookId);

}
