package ru.otus.hw.service;

import ru.otus.hw.domain.Answer;

import java.util.List;

public interface AnswerService {

    void showOptions(List<Answer> answers);

    boolean checkAnswer(int answerNumber, List<Answer> answers);

}
