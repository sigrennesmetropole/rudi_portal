package org.rudi.microservice.kalim.service.scheduler.harvesting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rudi.facet.providers.bean.NodeProvider;
import org.rudi.facet.providers.helper.ProviderHelper;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
class HarvestingTask implements Runnable {

	private final UUID targetProviderUuid;
	private final UUID targetNodeUuid;
	private final HarvestingHelper service;
	private final ProviderHelper providerHelper;

	@Override
	public void run() {
		logStatus("started");
		final LocalDateTime currentHarvestingDate = LocalDateTime.now();
		final NodeProvider targetNode = getTargetNode();
		service.harvest(targetNode).subscribe(
				integrationRequest -> log.info("Integration request submitted : {}", integrationRequest),
				error -> logStatus("failed", error),
				() -> onComplete(currentHarvestingDate)
		);
	}

	private NodeProvider getTargetNode() {
		return providerHelper.getNodeProviderByUUID(targetNodeUuid);
	}

	private void onComplete(LocalDateTime currentHarvestingDate) {
		providerHelper.patchNode(targetProviderUuid, targetNodeUuid, currentHarvestingDate).subscribe(
				updatedNode -> {},
				error -> log.error("Cannot save lastHarvestingDate for node {}", targetNodeUuid, error),
				() -> log.debug("lastHarvestingDate saved for node {}", targetNodeUuid)
		);
		logStatus("completed");
	}

	private void logStatus(String status) {
		logStatus(status, null);
	}

	private void logStatus(String status, @Nullable Throwable t) {
		log.info("{} node provider harvesting {}.", targetNodeUuid, status, t);
	}
}
