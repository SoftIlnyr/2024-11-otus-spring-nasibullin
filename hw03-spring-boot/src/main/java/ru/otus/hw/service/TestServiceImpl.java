package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final LocalizedIOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printLineLocalized("TestService.answer.the.questions");
        ioService.printLine("");

        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question: questions) {
            ioService.printLine(question.text());
            showOptions(question.answers());
            int answerNumber = ioService.readIntForRange(1, question.answers().size(), "Out of range");
            boolean isAnswerValid = checkAnswer(answerNumber, question.answers());
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private void showOptions(List<Answer> answers) {
        int answerIndex = 0;
        for (var answer: answers) {
            ioService.printLine(String.format("%d) %s", ++answerIndex, answer.text()));
        }
    }

    private boolean checkAnswer(int answerNumber, List<Answer> answers) {
        return answers.get(answerNumber - 1).isCorrect();
    }

}
