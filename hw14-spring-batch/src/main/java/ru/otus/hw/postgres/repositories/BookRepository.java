package ru.otus.hw.postgres.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.otus.hw.postgres.models.Book;

import java.util.List;
import java.util.Optional;

@Repository("pgBookRepository")
public interface BookRepository extends JpaRepository<Book, Long> {

    @Override
    @EntityGraph(value = "book-author-genres-entity-graph")
    Optional<Book> findById(Long id);

    @Override
    @EntityGraph(value = "book-author-entity-graph")
    List<Book> findAll();

}
