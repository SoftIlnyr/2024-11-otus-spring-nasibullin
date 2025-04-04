package ru.otus.hw.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.otus.hw.mongo.models.Book;

@Repository("mongoBookRepository")
public interface BookRepository extends MongoRepository<Book, String> {

}
