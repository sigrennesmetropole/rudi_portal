/**
 * RUDI Portail
 */
package org.rudi.microservice.kalim.service.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * @author FNI18300
 *
 */
@Configuration
public class SchedulerConfig implements SchedulingConfigurer {

	@Value("${rudi.kalim.scheduler.pool.size}")
	private int poolSize;

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

		scheduler.setPoolSize(poolSize);
		scheduler.setThreadNamePrefix("kalim-scheduled-task-pool-");
		scheduler.initialize();

		taskRegistrar.setTaskScheduler(scheduler);
	}
}
