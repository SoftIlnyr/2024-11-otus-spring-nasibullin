package ru.otus.hw.postgres.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.otus.hw.postgres.models.Genre;

import java.util.List;
import java.util.Set;

@Repository("pgGenreRepository")
public interface GenreRepository extends JpaRepository<Genre, Long> {

    List<Genre> findAllByIdIn(Set<Long> ids);
}
