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
import ru.otus.hw.mongo.models.Author;

import java.util.Collections;

@Configuration
public class AuthorMigrationConfig {

    private static final int AUTHOR_CHUNK_SIZE = 5;

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
    public Step migrateAuthorsStep() {
        return new StepBuilder("migrateAuthorStep", jobRepository)
                .<Author, ru.otus.hw.postgres.models.Author>chunk(AUTHOR_CHUNK_SIZE, platformTransactionManager)
                .reader(authorMongoReader())
                .processor(authorProcessor())
                .writer(authorPostgresWriter())
                .listener(new ItemWriteListener<>() {
                    @Override
                    public void afterWrite(Chunk<? extends ru.otus.hw.postgres.models.Author> authors) {
                        authors.forEach(author -> {
                            idCache.putAuthor(author.getMongoId(), author.getId());
                        });
                    }
                })
                .build();
    }

    @Bean
    public MongoPagingItemReader<Author> authorMongoReader() {
        return new MongoPagingItemReaderBuilder<Author>()
                .name("authorMongoReader")
                .template(mongoTemplate)
                .collection("authors")
                .targetType(Author.class)
                .query(new Query())
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .pageSize(AUTHOR_CHUNK_SIZE)
                .build();
    }

    @Bean
    public ItemProcessor<Author, ru.otus.hw.postgres.models.Author> authorProcessor() {
        return author -> {
            ru.otus.hw.postgres.models.Author authorPostgres = new ru.otus.hw.postgres.models.Author();
            authorPostgres.setMongoId(author.getId());
            authorPostgres.setFullName(author.getFullName());
            return authorPostgres;
        };
    }

    @Bean
    public ItemWriter<ru.otus.hw.postgres.models.Author> authorPostgresWriter() {
        JpaItemWriter<ru.otus.hw.postgres.models.Author> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        writer.setUsePersist(true);
        return writer;
    }
}
