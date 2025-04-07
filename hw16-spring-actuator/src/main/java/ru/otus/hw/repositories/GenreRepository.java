package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.otus.hw.models.Genre;

import java.util.List;

@RepositoryRestResource(path = "genres")
public interface GenreRepository extends MongoRepository<Genre, String> {

    List<Genre> findAllByIdIn(List<String> ids);
}
