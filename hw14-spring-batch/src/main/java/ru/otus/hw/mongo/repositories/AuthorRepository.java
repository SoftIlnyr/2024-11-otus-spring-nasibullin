package ru.otus.hw.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.otus.hw.mongo.models.Author;

@Repository("mongoAuthorRepository")
public interface AuthorRepository extends MongoRepository<Author, String> {
}
