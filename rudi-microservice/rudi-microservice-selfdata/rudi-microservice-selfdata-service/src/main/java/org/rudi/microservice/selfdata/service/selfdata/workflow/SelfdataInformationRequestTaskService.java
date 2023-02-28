package org.rudi.microservice.selfdata.service.selfdata.workflow;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import org.rudi.bpmn.core.bean.Form;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.facet.bpmn.exception.FormDefinitionException;
import org.rudi.facet.bpmn.exception.InvalidDataException;
import org.rudi.facet.bpmn.service.TaskService;
import org.rudi.microservice.selfdata.core.bean.SelfdataInformationRequest;

public interface SelfdataInformationRequestTaskService extends TaskService<SelfdataInformationRequest> {
	/**
	 * Recherche d'un formulaire DRAFT pour une demande d'information selfdata
	 *
	 * @param metadataUuid l'uuid du JDD concerné pour récupérer les données pivots
	 * @param language     chaîne de caractère représentant le language dans lequel on veut récupérer les libellés
	 * @return Un formulaire enrichi
	 * @throws AppServiceException si formulaire de base KO ou impossible de récupérer le JDD
	 */
	@Nullable
	Form lookupDraftFormWithSelfdata(UUID metadataUuid, Optional<String> language) throws AppServiceException, FormDefinitionException;

	/**
	 * @param taskId
	 * @return le formulaire draft pré-remplit d'avec les données saisies par l'user à une étape précédente
	 */
	Form lookupFilledMatchingDataForm(String taskId) throws FormDefinitionException, InvalidDataException;
}
