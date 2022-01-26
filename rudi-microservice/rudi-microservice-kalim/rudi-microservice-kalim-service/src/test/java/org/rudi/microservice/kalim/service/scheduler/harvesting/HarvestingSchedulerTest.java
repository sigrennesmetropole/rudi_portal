package org.rudi.microservice.kalim.service.scheduler.harvesting;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.facet.providers.bean.NodeProvider;
import org.rudi.facet.providers.helper.NodeWithProviderUuid;
import org.rudi.facet.providers.helper.ProviderHelper;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.rudi.microservice.kalim.service.scheduler.harvesting.HarvestingConfiguration.EVERY_HOUR_CRON;

@ExtendWith(MockitoExtension.class)
class HarvestingSchedulerTest {
	@InjectMocks
	private HarvestingScheduler harvestingScheduler;
	@Mock
	private ProviderHelper providerHelper;
	@Mock
	private TaskScheduler taskScheduler;
	@Mock
	private HarvestingConfiguration configuration;
	@Captor
	private ArgumentCaptor<HarvestingTask> scheduledHarvestingTaskCaptor;
	@Captor
	private ArgumentCaptor<CronTrigger> scheduledCronTriggerCaptor;

	@Test
	void scheduleAllCrons() {
		final NodeProvider node = new NodeProvider()
				.harvestingCron(EVERY_HOUR_CRON);
		final NodeWithProviderUuid nodeWithProviderUuid = new NodeWithProviderUuid(UUID.randomUUID(), node);
		final List<NodeWithProviderUuid> harvestableNodes = Collections.singletonList(nodeWithProviderUuid);

		when(providerHelper.getHarvestableNodes()).thenReturn(Mono.just(harvestableNodes));
		when(taskScheduler.schedule(scheduledHarvestingTaskCaptor.capture(), scheduledCronTriggerCaptor.capture())).thenReturn(null);

		harvestingScheduler.scheduleAllCrons();

		final HarvestingTask scheduledHarvestingTask = scheduledHarvestingTaskCaptor.getValue();
		assertThat(scheduledHarvestingTask)
				.hasFieldOrPropertyWithValue("targetProviderUuid", nodeWithProviderUuid.getProviderUuid())
				.hasFieldOrPropertyWithValue("targetNodeUuid", nodeWithProviderUuid.getNode().getUuid())
		;
		assertThat(scheduledCronTriggerCaptor.getValue()).hasFieldOrPropertyWithValue("expression", EVERY_HOUR_CRON);
	}

	@Test
	void scheduleAllCronsNodeWithoutHarvestingCron() {
		final NodeProvider node = new NodeProvider();
		final NodeWithProviderUuid nodeWithProviderUuid = new NodeWithProviderUuid(UUID.randomUUID(), node);
		final List<NodeWithProviderUuid> harvestableNodes = Collections.singletonList(nodeWithProviderUuid);

		when(providerHelper.getHarvestableNodes()).thenReturn(Mono.just(harvestableNodes));
		when(configuration.getDefaultCron()).thenReturn(EVERY_HOUR_CRON);
		when(taskScheduler.schedule(scheduledHarvestingTaskCaptor.capture(), scheduledCronTriggerCaptor.capture())).thenReturn(null);

		harvestingScheduler.scheduleAllCrons();

		final HarvestingTask scheduledHarvestingTask = scheduledHarvestingTaskCaptor.getValue();
		assertThat(scheduledHarvestingTask)
				.hasFieldOrPropertyWithValue("targetProviderUuid", nodeWithProviderUuid.getProviderUuid())
				.hasFieldOrPropertyWithValue("targetNodeUuid", nodeWithProviderUuid.getNode().getUuid())
		;
		assertThat(scheduledCronTriggerCaptor.getValue()).hasFieldOrPropertyWithValue("expression", EVERY_HOUR_CRON);
	}

	@Test
	void scheduleAllCronsProviderHelperError() {
		when(providerHelper.getHarvestableNodes()).thenReturn(Mono.error(new RuntimeException()));

		harvestingScheduler.scheduleAllCrons();

		verifyNoInteractions(taskScheduler);
	}

	@Test
	void scheduleAllCronsMultiple() {
		final NodeProvider node = new NodeProvider()
				.harvestingCron(EVERY_HOUR_CRON);
		final NodeWithProviderUuid nodeWithProviderUuid = new NodeWithProviderUuid(UUID.randomUUID(), node);
		final List<NodeWithProviderUuid> harvestableNodes = Arrays.asList(nodeWithProviderUuid, nodeWithProviderUuid);

		when(providerHelper.getHarvestableNodes()).thenReturn(Mono.just(harvestableNodes));

		harvestingScheduler.scheduleAllCrons();

		verify(taskScheduler, times(1)).schedule(any(HarvestingTask.class), any(CronTrigger.class));
	}
}
