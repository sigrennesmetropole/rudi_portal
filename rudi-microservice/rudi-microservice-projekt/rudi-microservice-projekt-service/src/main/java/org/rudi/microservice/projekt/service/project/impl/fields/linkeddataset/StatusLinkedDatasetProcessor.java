package org.rudi.microservice.projekt.service.project.impl.fields.linkeddataset;

import javax.annotation.Nullable;

import org.rudi.bpmn.core.bean.Status;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.bpmn.service.TaskService;
import org.rudi.microservice.projekt.core.bean.LinkedDataset;
import org.rudi.microservice.projekt.storage.entity.DatasetConfidentiality;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetStatus;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class StatusLinkedDatasetProcessor implements CreateLinkedDatasetFieldProcessor, UpdateLinkedDatasetFieldProcessor {

	private final UtilContextHelper utilContextHelper;

	private final TaskService<LinkedDataset> linkedDatasetTaskService;

	@Override
	public void process(@Nullable LinkedDatasetEntity linkedDataset, LinkedDatasetEntity existingLinkedDataset)
			throws AppServiceException {
		if (existingLinkedDataset != null) {
			existingLinkedDataset.setProcessDefinitionKey(linkedDatasetTaskService.getProcessDefinitionKey());
		}
		if (linkedDataset != null && existingLinkedDataset == null) {

			// JDD ouvert VALIDATED, la demande d'accès est autocomplétée
			if (linkedDataset.getDatasetConfidentiality() != null &&
					linkedDataset.getDatasetConfidentiality().equals(DatasetConfidentiality.OPENED)) {
				linkedDataset.setLinkedDatasetStatus(LinkedDatasetStatus.VALIDATED);
				linkedDataset.setStatus(Status.COMPLETED);
				linkedDataset.setFunctionalStatus("Demande d'accès ouverte complétée");
			}
			// JDD restreint, le workflow fera la suite on force à DRAFT en création + on gère la date de fin
			else {
				linkedDataset.setLinkedDatasetStatus(LinkedDatasetStatus.DRAFT);
				linkedDataset.setStatus(Status.DRAFT);
				linkedDataset.setFunctionalStatus("Demande créée");
			}

			linkedDataset.setProcessDefinitionKey(linkedDatasetTaskService.getProcessDefinitionKey());
			AuthenticatedUser authenticatedUser = utilContextHelper.getAuthenticatedUser();
			if (authenticatedUser != null) {
				linkedDataset.setInitiator(authenticatedUser.getLogin());
			}
		}
		if (linkedDataset != null && existingLinkedDataset != null) {
			// en modification si on est plus en draft on en peut plus changer les valeurs
			if (existingLinkedDataset.getStatus() != Status.DRAFT) {
				throw new AppServiceException(
						String.format("LinkedDataset %s is already handled and could not be modified",
								existingLinkedDataset.getUuid()));
			}
		}
	}

}
