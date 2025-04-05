package ru.otus.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannelSpec;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.PollerSpec;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.scheduling.PollerMetadata;
import ru.otus.hw.services.cleaning.CleaningService;
import ru.otus.hw.services.transfiguration.TransfigurationService;

@Configuration
public class IntegrationConfig {

	@Bean
	public MessageChannelSpec<?, ?> materialChannel() {
		return MessageChannels.queue(10);
	}

	@Bean
	public MessageChannelSpec<?, ?> goldChannel() {
		return MessageChannels.publishSubscribe();
	}

	@Bean(name = PollerMetadata.DEFAULT_POLLER)
	public PollerSpec poller() {
		return Pollers.fixedRate(100).maxMessagesPerPoll(2);
	}

	@Bean
	public IntegrationFlow cafeFlow(CleaningService cleaningService, TransfigurationService transfigurationService) {
		return IntegrationFlow.from(materialChannel())
				.split()
				.handle(cleaningService, "clean")
				.handle(transfigurationService, "transfigure")
				.aggregate()
				.channel(goldChannel())
				.get();
	}
}
