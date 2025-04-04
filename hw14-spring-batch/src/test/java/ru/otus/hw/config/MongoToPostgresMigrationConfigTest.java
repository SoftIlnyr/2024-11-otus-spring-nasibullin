package ru.otus.hw.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.otus.hw.mongock.changelog.TestValues;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static ru.otus.hw.config.MongoToPostgresMigrationConfig.MIGRATE_MONGO_TO_POSTGRES_JOB_NAME;

@SpringBootTest
@SpringBatchTest
class MongoToPostgresMigrationConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private IdCache idCache;

    @BeforeEach
    void clearMetaData() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    void testFullMigrationJob() throws Exception {
        // Запускаем job
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        Job job = jobLauncherTestUtils.getJob();
        assertThat(job).isNotNull()
                .extracting(Job::getName)
                .isEqualTo(MIGRATE_MONGO_TO_POSTGRES_JOB_NAME);

        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        JdbcTemplate jdbc = new JdbcTemplate(dataSource);

        Integer authorCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM authors", Integer.class);
        assertEquals(TestValues.AUTHORS.size(), authorCount);

        Integer bookCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM books", Integer.class);
        assertEquals(TestValues.BOOKS.size(), bookCount);

        Integer bookGenreCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM books_genres", Integer.class);
        Integer expectedGenresCount = (int) TestValues.BOOKS.stream().mapToLong(book -> book.getGenres().size()).sum();
        assertEquals(expectedGenresCount, bookGenreCount);

        Integer bookCommentCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM books_comments", Integer.class);
        assertEquals(TestValues.COMMENTS.size(), bookCommentCount);
    }
}