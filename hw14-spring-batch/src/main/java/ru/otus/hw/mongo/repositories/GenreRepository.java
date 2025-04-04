package ru.otus.hw.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.otus.hw.mongo.models.Genre;

import java.util.List;

@Repository("mongoGenreRepository")
public interface GenreRepository extends MongoRepository<Genre, String> {

    List<Genre> findAllByIdIn(List<String> ids);
}
