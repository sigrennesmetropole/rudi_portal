package org.rudi.microservice.kalim.service.scheduler.harvesting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.rudi.facet.providers.bean.NodeProvider;
import org.rudi.facet.providers.helper.NodeWithProviderUuid;
import org.rudi.facet.providers.helper.ProviderHelper;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.rudi.microservice.kalim.service.scheduler.harvesting.HarvestingConfiguration.DEFAULT_DELAY;
import static org.rudi.microservice.kalim.service.scheduler.harvesting.HarvestingConfiguration.PREFIX;

@Component
@Slf4j
@RequiredArgsConstructor
public class HarvestingScheduler {

	private final ProviderHelper providerHelper;
	private final TaskScheduler taskScheduler;
	private final HarvestingConfiguration configuration;
	private final HarvestingHelper service;
	private final Set<UUID> scheduledNodeUuids = new HashSet<>();

	@Scheduled(fixedDelayString = "${" + PREFIX + ".delay:" + DEFAULT_DELAY + "}")
	public void scheduleAllCrons() {
		log.info("Start {}...", getClass().getSimpleName());
		providerHelper.getHarvestableNodes().subscribe(
				nodeProviders -> nodeProviders.forEach(this::scheduleCronFor),
				e -> log.error("Cannot get harvestable nodes", e),
				() -> log.info("{} done.", getClass().getSimpleName())
		);
	}

	private void scheduleCronFor(NodeWithProviderUuid nodeWithProviderUuid) {
		final NodeProvider node = nodeWithProviderUuid.getNode();
		if (!scheduledNodeUuids.contains(node.getUuid())) {
			final HarvestingTask task = createTaskFor(nodeWithProviderUuid);
			final String cron = getCronFor(node);
			final CronTrigger trigger = new CronTrigger(cron);
			taskScheduler.schedule(task, trigger);

			log.info("Scheduled node with cron {} : {}", cron, node);
			scheduledNodeUuids.add(node.getUuid());
		}
	}

	@Nonnull
	private HarvestingTask createTaskFor(NodeWithProviderUuid nodeWithProviderUuid) {
		final UUID providerUuid = nodeWithProviderUuid.getProviderUuid();
		final UUID nodeUuid = nodeWithProviderUuid.getNode().getUuid();
		return new HarvestingTask(providerUuid, nodeUuid, service, providerHelper);
	}

	@Nonnull
	private String getCronFor(NodeProvider nodeProvider) {
		return ObjectUtils.firstNonNull(nodeProvider.getHarvestingCron(), configuration.getDefaultCron());
	}
}
