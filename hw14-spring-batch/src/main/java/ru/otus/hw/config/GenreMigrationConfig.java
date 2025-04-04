package ru.otus.hw.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
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
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.mongo.models.Genre;
import ru.otus.hw.mongo.models.Genre;

import java.util.Collections;

@Configuration
public class GenreMigrationConfig {

    private static final int GENRE_CHUNK_SIZE = 5;

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

    @Bean
    public Step migrateGenresStep() {
        return new StepBuilder("migrateGenreStep", jobRepository)
                .<Genre, ru.otus.hw.postgres.models.Genre>chunk(GENRE_CHUNK_SIZE, platformTransactionManager)
                .reader(genreMongoReader())
                .processor(genreProcessor())
                .writer(genrePostgresWriter())
                .listener(new ItemWriteListener<>() {
                    @Override
                    public void afterWrite(Chunk<? extends ru.otus.hw.postgres.models.Genre> genres) {
                        genres.forEach(genre -> {
                            idCache.putGenre(genre.getMongoId(), genre.getId());
                        });
                    }
                })
                .build();
    }

    @Bean
    public MongoPagingItemReader<Genre> genreMongoReader() {
        return new MongoPagingItemReaderBuilder<Genre>()
                .name("genreMongoReader")
                .template(mongoTemplate)
                .collection("genres")
                .targetType(Genre.class)
                .query(new Query())
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .pageSize(GENRE_CHUNK_SIZE)
                .build();
    }

    @Bean
    public ItemProcessor<Genre, ru.otus.hw.postgres.models.Genre> genreProcessor() {
        return genre -> {
            ru.otus.hw.postgres.models.Genre genrePostgres = new ru.otus.hw.postgres.models.Genre();
            genrePostgres.setMongoId(genre.getId());
            genrePostgres.setName(genre.getName());
            return genrePostgres;
        };
    }

    @Bean
    public ItemWriter<ru.otus.hw.postgres.models.Genre> genrePostgresWriter() {
        JpaItemWriter<ru.otus.hw.postgres.models.Genre> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        writer.setUsePersist(true);
        return writer;
    }
}
