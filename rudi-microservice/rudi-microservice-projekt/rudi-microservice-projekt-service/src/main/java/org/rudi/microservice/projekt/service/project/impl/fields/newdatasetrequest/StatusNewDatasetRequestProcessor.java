package org.rudi.microservice.projekt.service.project.impl.fields.newdatasetrequest;

import javax.annotation.Nullable;

import org.rudi.bpmn.core.bean.Status;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.bpmn.service.TaskService;
import org.rudi.microservice.projekt.core.bean.NewDatasetRequest;
import org.rudi.microservice.projekt.storage.entity.newdatasetrequest.NewDatasetRequestEntity;
import org.rudi.microservice.projekt.storage.entity.newdatasetrequest.NewDatasetRequestStatus;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class StatusNewDatasetRequestProcessor
		implements CreateNewDatasetRequestFieldProcessor, UpdateNewDatasetRequestFieldProcessor {

	private final UtilContextHelper utilContextHelper;

	private final TaskService<NewDatasetRequest> newDatasetRequestTaskService;

	@Override
	public void process(@Nullable NewDatasetRequestEntity newDatasetRequest,
			NewDatasetRequestEntity existingNewDatasetRequest) throws AppServiceException {
		if (existingNewDatasetRequest != null) {
			existingNewDatasetRequest.setProcessDefinitionKey(newDatasetRequestTaskService.getProcessDefinitionKey());
		}
		if (newDatasetRequest != null && existingNewDatasetRequest == null) {
			// on force à DRAFT en création
			newDatasetRequest.setProcessDefinitionKey(newDatasetRequestTaskService.getProcessDefinitionKey());
			newDatasetRequest.setNewDatasetRequestStatus(NewDatasetRequestStatus.DRAFT);
			newDatasetRequest.setStatus(Status.DRAFT);
			newDatasetRequest.setFunctionalStatus("Demande créée");
			AuthenticatedUser authenticatedUser = utilContextHelper.getAuthenticatedUser();
			if (authenticatedUser != null) {
				newDatasetRequest.setInitiator(authenticatedUser.getLogin());
			}
		}
		if (newDatasetRequest != null && existingNewDatasetRequest != null) {
			// en modification si on est plus en draft on en peut plus changer les valeurs
			if (existingNewDatasetRequest.getStatus() != Status.DRAFT) {
				throw new AppServiceException(
						String.format("NewDatasetRequest %s is already handled and could not be modified",
								existingNewDatasetRequest.getUuid()));
			}
		}
	}

}
