package org.rudi.microservice.projekt.service.project.impl.fields.linkeddataset;

import javax.annotation.Nullable;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceForbiddenException;
import org.rudi.facet.bpmn.service.TaskService;
import org.rudi.microservice.projekt.core.bean.LinkedDataset;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@Order(1)
@RequiredArgsConstructor
class LinkedDatasetTaskProcessor implements DeleteLinkedDatasetFieldProcessor {

	private final TaskService<LinkedDataset> linkedDatasetTaskService;

	@Override
	public void process(@Nullable LinkedDatasetEntity project, LinkedDatasetEntity existingProject)
			throws AppServiceException {
		if (existingProject == null) {
			return;
		}
		if (linkedDatasetTaskService.hasTask(existingProject.getUuid())) {
			throw new AppServiceForbiddenException(
					String.format("LinkedDataset %s has a running task", existingProject.getUuid()));
		}
	}

}
