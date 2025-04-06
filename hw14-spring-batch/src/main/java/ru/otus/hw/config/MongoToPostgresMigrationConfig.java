package ru.otus.hw.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoToPostgresMigrationConfig {

    public static final String MIGRATE_MONGO_TO_POSTGRES_JOB_NAME = "migrateMongoToPostgresJob";

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private AuthorMigrationConfig authorMigrationConfig;

    @Autowired
    private GenreMigrationConfig genreMigrationConfig;

    @Autowired
    private BookMigrationConfig bookMigrationConfig;

    @Autowired
    private CommentMigrationConfig commentMigrationConfig;

    @Bean
    public Job migrateMongoToPostgresJob() {
        JobBuilder jobBuilder = new JobBuilder(MIGRATE_MONGO_TO_POSTGRES_JOB_NAME, jobRepository);

        return jobBuilder.start(authorMigrationConfig.migrateAuthorsStep())
                .next(genreMigrationConfig.migrateGenresStep())
                .next(bookMigrationConfig.migrateBooksStep())
                .next(commentMigrationConfig.migrateCommentsStep())
                .build();
    }

}
