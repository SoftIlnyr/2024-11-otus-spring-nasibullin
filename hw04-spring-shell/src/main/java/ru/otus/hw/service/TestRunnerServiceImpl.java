package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import ru.otus.hw.security.LoginContext;

@ShellComponent(value = "Test Service")
@RequiredArgsConstructor
public class TestRunnerServiceImpl implements TestRunnerService {

    private final LoginContext loginContext;

    private final TestService testService;

    private final StudentService studentService;

    private final ResultService resultService;

    private final LocalizedIOService ioService;

    @ShellMethod(value = "Login", key = {"l", "lin", "login"})
    public void login() {
        var student = studentService.determineCurrentStudent();
        loginContext.login(student);
        ioService.printLineLocalized("StudentService.login.success");
    }

    @ShellMethod(value = "Logout", key = {"lout", "logout"})
    public void logout() {
        loginContext.logout();
        ioService.printLineLocalized("StudentService.logout.success");
    }

    @ShellMethod(value = "Start test", key = {"s", "start"})
    @ShellMethodAvailability(value = "isTestingAvailable")
    @Override
    public void runTest() {
        var testResult = testService.executeTestFor(loginContext.getStudent());
        resultService.showResult(testResult);
    }

    private Availability isTestingAvailable() {
        return loginContext.isUserLoggedIn()
                ? Availability.available()
                : Availability.unavailable(ioService.getMessage("TestService.login.required"));
    }
}
