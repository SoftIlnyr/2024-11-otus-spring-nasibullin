package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Answer;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final IOService ioService;

    @Override
    public void showOptions(List<Answer> answers) {
        int answerIndex = 0;
        for (var answer: answers) {
            ioService.printLine(String.format("%d) %s", ++answerIndex, answer.text()));
            answerIndex++;
        }
    }

    @Override
    public boolean checkAnswer(int answerNumber, List<Answer> answers) {
        int index = 1;
        for (var answer: answers) {
            if (index != answerNumber) {
                index++;
                continue;
            }
            return answer.isCorrect();
        }
        return false;
    }
}
