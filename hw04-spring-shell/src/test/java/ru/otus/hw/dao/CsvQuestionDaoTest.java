package ru.otus.hw.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
class CsvQuestionDaoTest {


    @Autowired
    CsvQuestionDao csvQuestionDao;

    @Test
    void findAll_fileExists() {
        TestFileNameProvider testFileNameProvider = Mockito.mock(TestFileNameProvider.class);
        when(testFileNameProvider.getTestFileName()).thenReturn("existing_questions.csv");
        List<Question> questions = csvQuestionDao.findAll();
        Assertions.assertTrue(questions.size() >= 3);
    }

    @Test
    void findAll_fileNotExists() {
        TestFileNameProvider testFileNameProvider = Mockito.mock(TestFileNameProvider.class);
        when(testFileNameProvider.getTestFileName()).thenReturn("no_questions.csv");
        CsvQuestionDao csvQuestionDao = new CsvQuestionDao(testFileNameProvider);
        Assertions.assertThrows(QuestionReadException.class, csvQuestionDao::findAll);
    }
}