package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import ru.otus.hw.security.LoginContext;

@Command(group = "Test Service")
@RequiredArgsConstructor
public class TestRunnerServiceImpl implements TestRunnerService {

    private final LoginContext loginContext;

    private final TestService testService;

    private final StudentService studentService;

    private final ResultService resultService;

    private final LocalizedIOService ioService;

    @Command(description = "Login", command = "login", alias = {"l", "lin"})
    public void login() {
        var student = studentService.determineCurrentStudent();
        loginContext.login(student);
        ioService.printLineLocalized("StudentService.login.success");
    }

    @Command(description = "Logout", command = "logout", alias =  {"lout"})
    public void logout() {
        loginContext.logout();
        ioService.printLineLocalized("StudentService.logout.success");
    }

    @Command(description = "Start test", command = "start", alias = {"s"})
    @CommandAvailability(provider = "testingProvider")
    @Override
    public void runTest() {
        var testResult = testService.executeTestFor(loginContext.getStudent());
        resultService.showResult(testResult);
    }

}
