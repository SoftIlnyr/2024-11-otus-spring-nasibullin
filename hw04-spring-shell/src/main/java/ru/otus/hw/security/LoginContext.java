package ru.otus.hw.security;

import ru.otus.hw.domain.Student;

public interface LoginContext {
    void login(Student student);

    void logout();

    boolean isUserLoggedIn();

    Student getStudent();
}
