package ru.otus.hw.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.otus.hw.mongo.models.Comment;

import java.util.List;

@Repository("mongoCommentRepository")
public interface CommentRepository extends MongoRepository<Comment, String> {

    List<Comment> findByBookId(String bookId);

    void deleteByBookId(String bookId);

}
