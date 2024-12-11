package ru.otus.hw.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.exceptions.QuestionReadException;

import static org.mockito.Mockito.when;

class CsvQuestionDaoTest {

    @Test
    void findAll_fileExists() {
        TestFileNameProvider testFileNameProvider = Mockito.mock(TestFileNameProvider.class);
        when(testFileNameProvider.getTestFileName()).thenReturn("existing_questions.csv");
        CsvQuestionDao csvQuestionDao = new CsvQuestionDao(testFileNameProvider);
        csvQuestionDao.findAll();
    }

    @Test
    void findAll_fileNotExists() {
        TestFileNameProvider testFileNameProvider = Mockito.mock(TestFileNameProvider.class);
        when(testFileNameProvider.getTestFileName()).thenReturn("no_questions.csv");
        CsvQuestionDao csvQuestionDao = new CsvQuestionDao(testFileNameProvider);
        Assertions.assertThrows(QuestionReadException.class, csvQuestionDao::findAll);
    }
}