package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    private final AnswerService answerService;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question: questions) {
            ioService.printLine(question.text());
            answerService.showOptions(question.answers());
            int answerNumber = ioService.readIntForRange(1, question.answers().size(), "Out of range");
            boolean isAnswerValid = answerService.checkAnswer(answerNumber, question.answers());
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }
}
