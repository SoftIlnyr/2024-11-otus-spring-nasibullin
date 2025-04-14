package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.otus.hw.services.AlchemyLaboratoryService;

@Component
@RequiredArgsConstructor
public class AppRunner implements CommandLineRunner {
	final AlchemyLaboratoryService alchemyLaboratoryService;

	@Override
	public void run(String... args) {
		alchemyLaboratoryService.makeGold();
	}
}
