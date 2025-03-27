package ru.otus.hw.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoPagingItemReader;
import org.springframework.batch.item.data.builder.MongoPagingItemReaderBuilder;
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
import ru.otus.hw.mongo.models.Book;
import ru.otus.hw.postgres.models.Author;
import ru.otus.hw.postgres.models.Genre;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

@Log4j2
@Configuration
public class BookMigrationConfig {

    private static final int BOOK_CHUNK_SIZE = 5;

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
    public Step migrateBooksStep() {
        return new StepBuilder("migrateBookStep", jobRepository)
                .<Book, ru.otus.hw.postgres.models.Book>chunk(BOOK_CHUNK_SIZE, platformTransactionManager)
                .reader(bookMongoReader())
                .processor(bookProcessor())
                .writer(bookPostgresWriter())
                .build();
    }

    @Bean
    public MongoPagingItemReader<Book> bookMongoReader() {
        return new MongoPagingItemReaderBuilder<Book>()
                .name("bookMongoReader")
                .template(mongoTemplate)
                .collection("books")
                .targetType(Book.class)
                .query(new Query())
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .pageSize(BOOK_CHUNK_SIZE)
                .build();
    }

    @Bean
    public ItemProcessor<Book, ru.otus.hw.postgres.models.Book> bookProcessor() {
        return bookMongo -> {
            ru.otus.hw.postgres.models.Book bookPostgres = new ru.otus.hw.postgres.models.Book();
            bookPostgres.setMongoId(bookMongo.getId());
            bookPostgres.setTitle(bookMongo.getTitle());

            EntityManager entityManager = entityManagerFactory.createEntityManager();

            var authorPostgres = entityManager.getReference(Author.class, idCache.getAuthor(bookMongo.getAuthor().getId()));
            bookPostgres.setAuthor(authorPostgres);

            List<Genre> genresPostgres = bookMongo.getGenres().stream()
                    .map(genreMongo -> {
                        var genrePostgres = entityManager.getReference(Genre.class, idCache.getGenre(genreMongo.getId()));
                        return genrePostgres;
                    }).toList();

            bookPostgres.setGenres(genresPostgres);

            entityManager.close();

            return bookPostgres;
        };
    }

    @Bean
    public ItemWriter<ru.otus.hw.postgres.models.Book> bookPostgresWriter() {
        return books -> {
            JdbcTemplate jdbc = new JdbcTemplate(dataSource);
            books.forEach(book -> {
                KeyHolder keyHolder = new GeneratedKeyHolder();

                jdbc.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO books (title, author_id) VALUES (?, ?)",
                            new String[]{"id"}
                    );
                    ps.setString(1, book.getTitle());
                    ps.setLong(2, book.getAuthor().getId());
                    return ps;
                }, keyHolder);

                Long bookId = keyHolder.getKey().longValue();
                idCache.putBook(book.getMongoId(), bookId);
                log.info("Added book to cache: mongoId {}, postgresId {}", book.getMongoId(), idCache.getBook(book.getMongoId()));

                // Вставка связей с жанрами
                jdbc.batchUpdate(
                        "INSERT INTO books_genres (book_id, genre_id) VALUES (?, ?)",
                        book.getGenres(),
                        100,
                        (ps, genre) -> {
                            ps.setLong(1, bookId);
                            ps.setLong(2, genre.getId());
                        }
                );
            });
        };
    }
}
