package ru.otus.hw.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class BookServiceHealthIndicator implements HealthIndicator {

    private static final int MAX_RETRIES = 3;

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    @Override
    public Health health() {
        int currentValue = COUNTER.incrementAndGet();
        boolean serverIsUp = currentValue <= MAX_RETRIES;
        if (serverIsUp) {
            int retriesLeft = MAX_RETRIES - currentValue;
            return Health.up()
                    .withDetail("message", "Сервис запущен, осталось попыток %d".formatted(retriesLeft))
                    .build();
        } else {
            COUNTER.set(0);
            return Health.down()
                    .status(Status.DOWN)
                    .withDetail("message", "Сервер на отдыхе, приходите позже.")
                    .build();
        }
    }
}
