package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.otus.hw.models.Author;

@RepositoryRestResource(path = "authors")
public interface AuthorRepository extends MongoRepository<Author, String> {
}
