package ru.otus.hw.postgres.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.otus.hw.postgres.models.Author;

@Repository("pgAuthorRepository")
public interface AuthorRepository extends JpaRepository<Author, Long> {
}
