package ru.otus.hw.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class TestServiceImplTest {

    @InjectMocks
    private TestServiceImpl testService;
    @Mock
    private QuestionDao questionDao;
    @Mock
    private LocalizedIOService ioService;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void executeTestFor() {
        Student student = new Student("Test", "Student");
        List<Question> questions = new ArrayList<>();
        Question question1 = new Question("question1", List.of(
            new Answer("answer11", true),
            new Answer("answer12", false),
            new Answer("answer13", false)
        ));
        questions.add(question1);
        when(questionDao.findAll()).thenReturn(questions);
        when(ioService.readIntForRange(anyInt(), anyInt(), nullable(String.class))).thenReturn(1);
        TestResult testResult = testService.executeTestFor(student);
        Assertions.assertTrue(testResult.getAnsweredQuestions().contains(question1));
    }
}