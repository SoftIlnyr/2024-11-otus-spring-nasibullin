package ru.otus.hw.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.time.Duration;

@Configuration
@Import(FeignClientsConfiguration.class)
public class CloudConfig {

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(100))
                        .build())
                .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                .build());
    }

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> bookCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(500))
                        .build())
                .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                .build());
    }

    @Bean
    public CircuitBreaker defaultCircuitBreaker(CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        return circuitBreakerFactory.create("defaultCircuitBreaker");
    }

    @Bean
    public CircuitBreaker bookCircuitBreaker(CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        return circuitBreakerFactory.create("bookCircuitBreaker");
    }

}
