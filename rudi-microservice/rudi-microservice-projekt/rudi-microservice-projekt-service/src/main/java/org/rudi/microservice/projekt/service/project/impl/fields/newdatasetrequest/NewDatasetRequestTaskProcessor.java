package org.rudi.microservice.projekt.service.project.impl.fields.newdatasetrequest;

import javax.annotation.Nullable;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceForbiddenException;
import org.rudi.facet.bpmn.service.TaskService;
import org.rudi.microservice.projekt.core.bean.NewDatasetRequest;
import org.rudi.microservice.projekt.storage.entity.newdatasetrequest.NewDatasetRequestEntity;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class NewDatasetRequestTaskProcessor implements DeleteNewDatasetRequestFieldProcessor {

	private final TaskService<NewDatasetRequest> newDatasetRequestTaskService;

	@Override
	public void process(@Nullable NewDatasetRequestEntity project, NewDatasetRequestEntity existingProject)
			throws AppServiceException {
		if (existingProject == null) {
			return;
		}
		if (newDatasetRequestTaskService.hasTask(existingProject.getUuid())) {
			throw new AppServiceForbiddenException(
					String.format("NewDatasetRequest %s has a running task", existingProject.getUuid()));
		}
	}

}
