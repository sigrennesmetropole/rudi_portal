package org.rudi.microservice.kalim.service.scheduler.harvesting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

@Configuration
@ConfigurationProperties(prefix = HarvestingConfiguration.PREFIX)
@RequiredArgsConstructor
@Getter
@Setter
@SuppressWarnings("java:S1075") // Les URL par défaut des paramètres sont obligatoirement en dur dans le code
class HarvestingConfiguration {

	public static final String PREFIX = "rudi.kalim.scheduler.harvesting";
	public static final long DEFAULT_DELAY = 30000L;

	/**
	 * Query parameter name for #resourcesPath request
	 */
	public static final String OFFSET = "offset";

	/**
	 * Query parameter name for #resourcesPath request
	 */
	public static final String UPDATED_AFTER = "updated_after";

	static final String EVERY_HOUR_CRON = "0 0 * * * *";

	/**
	 * Delay to check nodes harvesting to schedule with cron in millisecond
	 */
	private long delay = DEFAULT_DELAY;

	private String defaultCron = EVERY_HOUR_CRON;

	/**
	 * URL to call on a node to harvest it
	 */
	private String resourcesPath = "/resources";

	@Bean
	public TaskScheduler taskScheduler() {
		return new ConcurrentTaskScheduler();
	}

}
