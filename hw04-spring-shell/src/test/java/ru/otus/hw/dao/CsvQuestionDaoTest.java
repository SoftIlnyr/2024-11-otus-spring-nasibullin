package ru.otus.hw.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.config.LocaleConfig;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;
import ru.otus.hw.service.TestRunnerService;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CsvQuestionDaoTest {

    @MockBean
    TestRunnerService testRunnerService;
//    @MockBean
//    TestFileNameProvider testFileNameProvider;

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