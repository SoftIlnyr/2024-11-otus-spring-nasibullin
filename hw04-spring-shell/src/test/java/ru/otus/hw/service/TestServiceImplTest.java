package ru.otus.hw.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

@SpringBootTest
class TestServiceImplTest {

    @MockitoBean
    TestRunnerService testRunnerService;

    @Autowired
    private TestService testService;
    @MockitoBean
    private QuestionDao questionDao;
    @MockitoBean
    private LocalizedIOService ioService;

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