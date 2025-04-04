package ru.otus.hw.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoPagingItemReader;
import org.springframework.batch.item.data.builder.MongoPagingItemReaderBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.mongo.models.Comment;
import ru.otus.hw.postgres.models.Author;
import ru.otus.hw.postgres.models.Book;
import ru.otus.hw.postgres.models.Genre;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;

@Log4j2
@Configuration
public class CommentMigrationConfig {

    private static final int COMMENT_CHUNK_SIZE = 5;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private IdCache idCache;

    @Autowired
    private DataSource dataSource;

    @Bean
    public Step migrateCommentsStep() {
        return new StepBuilder("migrateCommentStep", jobRepository)
                .<Comment, ru.otus.hw.postgres.models.Comment>chunk(COMMENT_CHUNK_SIZE, platformTransactionManager)
                .reader(commentMongoReader())
                .processor(commentProcessor())
                .writer(commentPostgresWriter())
                .build();
    }

    @Bean
    public MongoPagingItemReader<Comment> commentMongoReader() {
        return new MongoPagingItemReaderBuilder<Comment>()
                .name("commentMongoReader")
                .template(mongoTemplate)
                .collection("books_comments")
                .targetType(Comment.class)
                .query(new Query())
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .pageSize(COMMENT_CHUNK_SIZE)
                .build();
    }

    @Bean
    public ItemProcessor<Comment, ru.otus.hw.postgres.models.Comment> commentProcessor() {
        return commentMongo -> {
            ru.otus.hw.postgres.models.Comment commentPostgres = new ru.otus.hw.postgres.models.Comment();
            commentPostgres.setMongoId(commentMongo.getId());
            commentPostgres.setText(commentMongo.getText());

            EntityManager entityManager = entityManagerFactory.createEntityManager();

            var bookPostgres = entityManager.getReference(Book.class, idCache.getBook(commentMongo.getBook().getId()));
            commentPostgres.setBook(bookPostgres);

            entityManager.close();

            return commentPostgres;
        };
    }

    @Bean
    public ItemWriter<ru.otus.hw.postgres.models.Comment> commentPostgresWriter() {
        return comments -> {
            JdbcTemplate jdbc = new JdbcTemplate(dataSource);
            comments.forEach(comment -> {
                KeyHolder keyHolder = new GeneratedKeyHolder();

                jdbc.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO books_comments (book_id, text) VALUES (?, ?)",
                            new String[]{"id"}
                    );
                    ps.setLong(1, comment.getBook().getId());
                    ps.setString(2, comment.getText());
                    return ps;
                }, keyHolder);

                Long commentId = keyHolder.getKey().longValue();
                idCache.putBook(comment.getMongoId(), commentId);
                log.info("Added comment to cache: mongoId {}, postgresId {}", comment.getMongoId(),
                        idCache.getComment(comment.getMongoId()));
            });
        };
    }
}
