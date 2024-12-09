package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

@Setter
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;
    private QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        // Получить вопросы из дао и вывести их с вариантами ответов
        List<Question> questions = questionDao.findAll();
        for (Question question : questions) {
            ioService.printLine(question.text());
            for (Answer answer : question.answers()) {
                ioService.printLine(answer.text());
            }
            ioService.printLine("");
        }
    }

}
